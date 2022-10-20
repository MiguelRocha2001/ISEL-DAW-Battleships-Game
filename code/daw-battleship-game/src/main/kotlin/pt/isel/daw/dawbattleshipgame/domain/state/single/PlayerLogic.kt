package pt.isel.daw.dawbattleshipgame.domain.state.single

import pt.isel.daw.dawbattleshipgame.domain.board.*
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.getOrientation
import pt.isel.daw.dawbattleshipgame.domain.ship.getShip
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import kotlin.collections.first


enum class PlayerState {WAITING, PREPARATION}



class PlayerLogic(
    val state : PlayerState,
    val gameId: Int,
    val playerId: Int,
    val configuration: Configuration,
    val board: Board,
) {
    private fun createGamePhase(board: Board) = PlayerPhase(
        gameId, configuration, playerId,board, state
    )

    private val coordinates = Coordinates(configuration.boardSize)

    /**
     * Places a ship on the board.
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGamePlaceShip(old: PlayerLogic, shipType: ShipType, coordinate: Coordinate, orientation: Orientation): PlayerPhase {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = old.generateShipCoordinates(shipType, coordinate, orientation) ?: throw Exception()
        return createGamePhase(board.placeShip(shipCoordinates, shipType))
    }

    /**
     * Builds a new Game object, with ship removed from [position]
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGameRemovedShip(old: PlayerLogic, position: Coordinate): PlayerPhase {
        if(old.isNotShip(position)) throw Exception()
        val ship = old.board.getShips().getShip(position)
        val shipCoordinates = ship.coordinates
        return createGamePhase(board.placeWaterPanel(shipCoordinates))
    }

    private fun buildGameMoveShip(old : PlayerLogic, coordinateS: CoordinateSet, shipType: ShipType): PlayerPhase {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        return createGamePhase(board.placeShip(coordinateS, shipType))
    }

    private fun isShip(c: Coordinate) = board.isShip(c)
    private fun isNotShip(c: Coordinate) = !board.isShip(c)

    /**
     * Tries to place [shipType] on the Board, on give in [position].
     * @return updated Game or null, if is not possible to position [shipType] in [position]
     */
    fun tryPlaceShip(
        shipType: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): PlayerPhase? {
        if (isShipPlaced(shipType)) return null
        return try {
            buildGamePlaceShip(this, shipType, position, orientation)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Tries to place a ship, giving its coordinates and its type
     */
    private fun tryPlaceShipWithCoordinates(
        shipType: ShipType,
        coordinates: CoordinateSet,
    ) : PlayerPhase? {
        return try {
            buildGameMoveShip(this, coordinates, shipType)
        }catch (e : Exception){
            null
        }
    }

    /**
     * Generates a new Warmup Board with a moved ship
     */
    fun tryMoveShip(position: Coordinate, destination: Coordinate): PlayerPhase? {
        return try {
            val ship = board.getShips().getShip(position)
            val newCoordinates = ship.coordinates.moveFromTo(position, destination, configuration.boardSize)
            val a = isShipTouchingAnother(board, newCoordinates)
            if (isShipTouchingAnother(board, newCoordinates)) return null
            tryRemoveShip(position)?.logic?.tryPlaceShipWithCoordinates(ship.type, newCoordinates)
        }catch (e : Exception){
            null
        }
    }

    /**
     * Tries to remove ship from the game, if one exists.
     * @param position coordinate where the ship is located (some part of the ship)
     * @return new Game with ship removed or null if ship was not found, for [position]
     */
    private fun tryRemoveShip(position: Coordinate): PlayerPhase? {
        return try {
            buildGameRemovedShip(this, position)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Tries to rotate a ship, if possible.
     * @return newly created game, with ship rotated, or null if not possible
     */
    fun tryRotateShip(position: Coordinate): PlayerPhase? {
        return try {
            val ship = board.getShips().getShip(position)
            val curOrientation = ship.getOrientation()
            val shipPosOrigin = board.getShips().getShip(position).coordinates.first()
            val tmpGame = tryRemoveShip(position)
            tmpGame?.logic?.tryPlaceShip(ship.type, shipPosOrigin, curOrientation.other())
        }catch (e : Exception){
            null
        }
    }


    /**
     * @returns List of Coordinates with positions to build a ship or null if impossible
     */
    private fun generateShipCoordinates(ship: ShipType, position: Coordinate, orientation: Orientation): CoordinateSet? {
        if (isShip(position)) return null

        val shipCoordinates = tryGenerateShipPanels(
            getShipLength(ship), position, orientation
        ) ?: return null

        if (isShipTouchingAnother(board, shipCoordinates)) return null
        return shipCoordinates
    }

    /**
     * Detects if a Ship, given by [shipCoordinates] is touching another Ship (ShipPanel).
     */
    private fun isShipTouchingAnother(board: Board, shipCoordinates: CoordinateSet): Boolean =
        shipCoordinates.any { isShipNearCoordinate(it, board) }

    /**
     * Check if any ship from the player is near the coordinate
     */
    private fun isShipNearCoordinate(c: Coordinate, board: Board) =
        coordinates.radius(c).any { board.isShip(it) && (board[it].shipType != board[c].shipType) }

    /**
     * Generates the coordinates needed to make the ship
     */
    private fun tryGenerateShipPanels(size: Int, coordinate: Coordinate, orientation: Orientation): CoordinateSet? {
        var auxCoordinate = coordinate
        val set = mutableSetOf(coordinate)
        repeat(size - 1) {
            auxCoordinate = if (orientation === Orientation.HORIZONTAL)
                coordinates.right(auxCoordinate) ?: return null
            else
                coordinates.down(auxCoordinate) ?: return null
            set.add(auxCoordinate)
        }
        return set
    }

    /**
     * Retrieves the ship length according to class game configuration.
     */
    private fun getShipLength(shipType: ShipType) =
        configuration.fleet.first { it.first === shipType }.second

    private fun isShipPlaced(shipType: ShipType) =
        board.getShips().map { it.type }.any { it === shipType }

    fun confirmFleet() = PlayerPhase(gameId, configuration, playerId, board, PlayerState.WAITING)

}


class PlayerPhase(
    val gameId: Int,
    val configuration: Configuration,
    val playerId: Int,
    val board: Board = Board(configuration.boardSize),
    val state : PlayerState = PlayerState.PREPARATION,
){
    private fun checkState() {
        if (state == PlayerState.WAITING)
            throw IllegalAccessException("Player is waiting, unable to perform operations")
    }

    val logic: PlayerLogic
        get()  = checkState().run {
            PlayerLogic(state, gameId, playerId, configuration, board)
        }

    fun isWaiting() = state == PlayerState.WAITING
    fun isNotWaiting() = !isWaiting()

    override fun toString() = board.toString()
}

package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.board.*
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.*
import pt.isel.daw.dawbattleshipgame.domain.ship.types.getOrientation
import kotlin.collections.first


class Warmup: GameState {
    override val configuration: Configuration
    private val coordinates: Coordinates
    override val myBoard: Board

    /**
     * Creates a new Game.
     */
    constructor(configuration: Configuration) {
        this.configuration = configuration
        myBoard = Board(configuration.boardSize)
        coordinates = Coordinates(configuration.boardSize)
    }

    private constructor(newConfiguration: Configuration, newBoard: Board){
        configuration = newConfiguration
        myBoard = newBoard
        coordinates = Coordinates(configuration.boardSize)
    }

    /**
     * Places a ship on the board.
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGamePlaceShip(old: Warmup, shipType: ShipType, coordinate: Coordinate, orientation: Orientation): Warmup {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = old.generateShipCoordinates(shipType, coordinate, orientation) ?: throw Exception()
        return Warmup(old.configuration,
            old.myBoard.placeShipPanel(shipCoordinates, shipType),
        )
    }

    /**
     * Builds a new Game object, with ship removed from [position]
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGameRemovedShip(old: Warmup, position: Coordinate): Warmup {
        if(old.isNotShip(position)) throw Exception()
        val ship = old.myBoard.getShips().getShip(position)
        val shipCoordinates = ship.coordinates
        return Warmup(
            old.configuration,
            old.myBoard.placeWaterPanel(shipCoordinates),
        )
    }

    private fun buildGameMoveShip(old : Warmup, coordinateS: CoordinateSet, shipType: ShipType): Warmup {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        return Warmup(
            old.configuration,
            old.myBoard.placeShipPanel(coordinateS, shipType)
        )
    }

    operator fun get(coordinate: Coordinate): Panel {
        return myBoard[coordinate]
    }

    override fun toString(): String {
        return myBoard.toString()
    }

    private fun isShip(c: Coordinate) = myBoard.isShipPanel(c)
    private fun isNotShip(c: Coordinate) = myBoard.isWaterPanel(c)

    /**
     * Tries to place [shipType] on the Board, on give in [position].
     * @return updated Game or null, if is not possible to position [shipType] in [position]
     */
    fun tryPlaceShip(
        shipType: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): Warmup? {
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
    ) : Warmup? {
        return try {
            buildGameMoveShip(this, coordinates, shipType)
        }catch (e : Exception){
            null
        }
    }

    /**
     * Generates a new Warmup Board with a moved ship
     */
    fun tryMoveShip(position: Coordinate, destination: Coordinate): Warmup? {
        return try {
            val ship = myBoard.getShips().getShip(position)
            val newCoordinates = ship.coordinates.moveFromTo(position, destination, configuration.boardSize)
            tryRemoveShip(position)?.tryPlaceShipWithCoordinates(ship.type, newCoordinates)
        }catch (e : Exception){
            null
        }
    }

    /**
     * Tries to remove ship from the game, if one exists.
     * @param position coordinate where the ship is located (some part of the ship)
     * @return new Game with ship removed or null if ship was not found, for [position]
     */
    private fun tryRemoveShip(position: Coordinate): Warmup? {
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
    fun tryRotateShip(position: Coordinate): Warmup? {
        return try {
            val ship = myBoard.getShips().getShip(position)
            val curOrientation = ship.getOrientation()
            val shipPosOrigin = myBoard.getShips().getShip(position).coordinates.first()
            val tmpGame = tryRemoveShip(position)
            tmpGame?.tryPlaceShip(ship.type, shipPosOrigin, curOrientation.other())
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

        if (isShipTouchingAnother(myBoard, shipCoordinates)) return null
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
        coordinates.radius(c).any { board.isShipPanel(it) }


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
        myBoard.getShips().map { it.type }.any { it === shipType }
}
package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.*
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.getShip
import pt.isel.daw.dawbattleshipgame.domain.ship.types.getOrientation
import kotlin.collections.first

class PreparationPhase(
    override val gameId: Int,
    player1Id: String,
    player2Id: String,
    override val configuration: Configuration
) : Game() {

    val player1PreparationPhase: PlayerPreparationPhase
    val player2PreparationPhase: PlayerPreparationPhase

    init {
        this.player1PreparationPhase = PlayerPreparationPhase(gameId, configuration, player1Id)
        this.player2PreparationPhase = PlayerPreparationPhase(gameId, configuration, player2Id)
    }
}

class PlayerPreparationPhase {
    val gameId: Int
    val playerId: String

    val configuration: Configuration
    private val coordinates: Coordinates
    val board: Board

    /**
     * Creates a new Game.
     */
    constructor(gameId: Int, configuration: Configuration, playerId: String) {
        this.gameId = gameId
        this.configuration = configuration
        this.playerId = playerId

        board = Board(configuration.boardSize)
        coordinates = Coordinates(configuration.boardSize)
    }

    /**
     * Places a ship on the board.
     * @throws Exception if is not possible to place the ship
     */
    private constructor(old: PlayerPreparationPhase, shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = old.generateShipCoordinates(shipType, coordinate, orientation) ?: throw Exception()
        this.gameId = old.gameId
        this.playerId = old.playerId
        configuration = old.configuration
        board = old.board.placeShipPanel(shipCoordinates, shipType)
        coordinates = old.coordinates
    }

    /**
     * Builds a new Game object, with ship removed from [position]
     * @throws Exception if is not possible to place the ship
     */
    private constructor(old: PlayerPreparationPhase, position: Coordinate) {
        if(old.isNotShip(position)) throw Exception()

        val ship = old.board.getShips().getShip(position)
        val shipCoordinates = ship?.coordinates ?: throw Exception()

        this.gameId = old.gameId
        this.playerId = old.playerId
        configuration = old.configuration
        board = old.board.placeWaterPanel(shipCoordinates) // new Board with ship removed
        coordinates = old.coordinates
    }

    operator fun get(coordinate: Coordinate): Panel {
        return board[coordinate]
    }

    override fun toString(): String {
        return board.toString()
    }

    fun isShip(c: Coordinate) = board.isShipPanel(c)

    private fun isNotShip(c: Coordinate) = board.isWaterPanel(c)

    /**
     * Tries to place [shipType] on the Board, on give in [position].
     * @return updated Game or null, if is not possible to position [shipType] in [position]
     */
    fun tryPlaceShip(
        shipType: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): PlayerPreparationPhase? {
        if (isShipPlaced(shipType)) return null
        return try {
            PlayerPreparationPhase(this, shipType, position, orientation) // Builds Game with new ship
        } catch (e: Exception) {
            null
        }
    }

    fun tryMoveShip(position: Coordinate, destination: Coordinate): PlayerPreparationPhase? {
        val ship = board.getShips().getShip(position) ?: return null
        val orientation = ship.getOrientation()
        val computedDestination = getAppropriateCoordinateToMoveShipTo(position, destination, orientation) ?: return null

        val newGame = tryRemoveShip(position) ?: return null
        return newGame.tryPlaceShip(ship.type, computedDestination, orientation)
    }

    private fun getAppropriateCoordinateToMoveShipTo(
        position: Coordinate,
        destination: Coordinate,
        orientation: Orientation
    ): Coordinate? {
        val playerShips = board.getShips()
        val posIndex = playerShips.getShip(position)?.coordinates?.index(position) ?: return null // ship [position] index
        var computedDestination = destination
        repeat(posIndex) {
            computedDestination = if (orientation === Orientation.HORIZONTAL)
                coordinates.left(computedDestination) ?: return null
            else
                coordinates.up(computedDestination) ?: return null
        }
        return computedDestination
    }

    /**
     * Tries to remove ship from the game, if one exists.
     * @param position coordinate where the ship is located (some part of the ship)
     * @return new Game with ship removed or null if ship was not found, for [position]
     */
    private fun tryRemoveShip(position: Coordinate): PlayerPreparationPhase? {
        return try {
            PlayerPreparationPhase(this, position) // Builds new Game with ship removed
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Tries to rotate a ship, if possible.
     * @return newly created game, with ship rotated, or null if not possible
     */
    fun tryRotateShip(position: Coordinate): PlayerPreparationPhase? {
        val ship = board.getShips().getShip(position) ?: return null
        val curOrientation = ship.getOrientation()
        val shipPosOrigin = board.getShips().getShip(position)?.coordinates?.first() ?: return null
        val tmpGame = tryRemoveShip(position) ?: return null
        return tmpGame.tryPlaceShip(ship.type, shipPosOrigin, curOrientation.other())
    }

    fun tryRotateShip(position: String): Game? {
        TODO("Not yet implemented")
    }

    /**
     * @return Ship type, positioned at [position] or null if no Ship is placed there
     */
    private fun getShipType(position: Coordinate) = board.getShips().getShip(position)?.type

    /**
     * @returns List of Coordinates with positions to build a ship or null if impossible
     */
    private fun generateShipCoordinates(ship: ShipType, position: Coordinate, orientation: Orientation): CoordinateSet? {
        if (isShip(position)) return null
        val shipLength = getShipLength(ship)
        val shipCoordinates = tryGenerateShipPanels(shipLength, position, orientation) ?: return null
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
        board.getShips().map { it.type }.any { it === shipType }

    fun confirmFleet() = PlayerWaitingPhase(gameId, configuration, board)
}
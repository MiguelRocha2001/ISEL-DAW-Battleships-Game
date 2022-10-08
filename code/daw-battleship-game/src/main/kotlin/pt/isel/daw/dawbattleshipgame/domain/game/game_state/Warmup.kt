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

    /**
     * Places a ship on the board.
     * @throws Exception if is not possible to place the ship
     */
    private constructor(old: Warmup, shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = old.generateShipCoordinates(shipType, coordinate, orientation) ?: throw Exception()
        configuration = old.configuration
        myBoard = old.myBoard.placeShipPanel(shipCoordinates, shipType)
        coordinates = old.coordinates
    }

    /**
     * Builds a new Game object, with ship removed from [position]
     * @throws Exception if is not possible to place the ship
     */
    private constructor(old: Warmup, position: Coordinate) {
        if(old.isNotShip(position)) throw Exception()

        val ship = old.myBoard.getShips().getShip(position)
        val shipCoordinates = ship?.coordinates ?: throw Exception()

        configuration = old.configuration
        myBoard = old.myBoard.placeWaterPanel(shipCoordinates) // new Board with ship removed
        coordinates = old.coordinates
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
            Warmup(this, shipType, position, orientation) // Builds Game with new ship
        } catch (e: Exception) {
            null
        }
    }

    fun tryMoveShip(position: Coordinate, destination: Coordinate): Warmup? {
        val ship = myBoard.getShips().getShip(position) ?: return null
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
        val playerShips = myBoard.getShips()
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
    private fun tryRemoveShip(position: Coordinate): Warmup? {
        return try {
            Warmup(this, position) // Builds new Game with ship removed
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Tries to rotate a ship, if possible.
     * @return newly created game, with ship rotated, or null if not possible
     */
    fun tryRotateShip(position: Coordinate): Warmup? {
        val ship = myBoard.getShips().getShip(position) ?: return null
        val curOrientation = ship.getOrientation()
        val shipPosOrigin = myBoard.getShips().getShip(position)?.coordinates?.first() ?: return null
        val tmpGame = tryRemoveShip(position) ?: return null
        return tmpGame.tryPlaceShip(ship.type, shipPosOrigin, curOrientation.other())
    }

    /**
     * @return Ship type, positioned at [position] or null if no Ship is placed there
     */
    private fun getShipType(position: Coordinate) = myBoard.getShips().getShip(position)?.type

    /**
     * @returns List of Coordinates with positions to build a ship or null if impossible
     */
    private fun generateShipCoordinates(ship: ShipType, position: Coordinate, orientation: Orientation): CoordinateSet? {
        if (isShip(position)) return null
        val shipLength = getShipLength(ship)
        val shipCoordinates = tryGenerateShipPanels(shipLength, position, orientation) ?: return null
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

    private fun isShipPlaced(shipType: ShipType) = myBoard.getShips().map { it.type }.isEmpty()
}
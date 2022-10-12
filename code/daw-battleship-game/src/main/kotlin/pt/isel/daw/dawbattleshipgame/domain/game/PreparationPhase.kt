package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.*
import pt.isel.daw.dawbattleshipgame.domain.ship.*
import pt.isel.daw.dawbattleshipgame.domain.ship.types.getOrientation
import kotlin.collections.first

import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.getShip

class PreparationPhase(
    override val gameId: Int,
    override val configuration: Configuration,
    override val player1: String,
    override val player2: String,
) : Game() {

    val player1PreparationPhase: PlayerPreparationPhase
    val player2PreparationPhase: PlayerPreparationPhase

    init {
        this.player1PreparationPhase = PlayerPreparationPhase(gameId, configuration, player1)
        this.player2PreparationPhase = PlayerPreparationPhase(gameId, configuration, player2)
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
        this.configuration = configuration
        this.gameId = gameId
        this.playerId = playerId
        board = Board(configuration.boardSize)
        coordinates = Coordinates(configuration.boardSize)
    }

    private constructor(old: PlayerPreparationPhase, newBoard: Board){
        this.configuration = old.configuration
        this.gameId = old.gameId
        this.playerId = old.playerId
        board = newBoard
        coordinates = Coordinates(configuration.boardSize)
    }

    /**
     * Places a ship on the board.
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGamePlaceShip(old: PlayerPreparationPhase, shipType: ShipType, coordinate: Coordinate, orientation: Orientation): PlayerPreparationPhase {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = old.generateShipCoordinates(shipType, coordinate, orientation) ?: throw Exception()
        return PlayerPreparationPhase(this,
            old.board.placeShipPanel(shipCoordinates, shipType),
        )
    }

    /**
     * Builds a new Game object, with ship removed from [position]
     * @throws Exception if is not possible to place the ship
     */
    private fun buildGameRemovedShip(old: PlayerPreparationPhase, position: Coordinate): PlayerPreparationPhase {
        if(old.isNotShip(position)) throw Exception()
        val ship = old.board.getShips().getShip(position)
        val shipCoordinates = ship.coordinates
        return PlayerPreparationPhase(
            this,
            old.board.placeWaterPanel(shipCoordinates),
        )
    }

    private fun buildGameMoveShip(old : PlayerPreparationPhase, coordinateS: CoordinateSet, shipType: ShipType): PlayerPreparationPhase {
        if (!old.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        return PlayerPreparationPhase(
            this,
            old.board.placeShipPanel(coordinateS, shipType)
        )
    }

    operator fun get(coordinate: Coordinate): Panel {
        return board[coordinate]
    }

    override fun toString(): String {
        return board.toString()
    }

    private fun isShip(c: Coordinate) = board.isShipPanel(c)
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
    ) : PlayerPreparationPhase? {
        return try {
            buildGameMoveShip(this, coordinates, shipType)
        }catch (e : Exception){
            null
        }
    }

    /**
     * Generates a new Warmup Board with a moved ship
     */
    fun tryMoveShip(position: Coordinate, destination: Coordinate): PlayerPreparationPhase? {
        return try {
            val ship = board.getShips().getShip(position)
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
    private fun tryRemoveShip(position: Coordinate): PlayerPreparationPhase? {
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
    fun tryRotateShip(position: Coordinate): PlayerPreparationPhase? {
        return try {
            val ship = board.getShips().getShip(position)
            val curOrientation = ship.getOrientation()
            val shipPosOrigin = board.getShips().getShip(position).coordinates.first()
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
        coordinates.radius(c)!!.any { board.isShipPanel(it) }


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

    fun confirmFleet() = PlayerWaitingPhase(gameId, configuration, board, playerId)
}
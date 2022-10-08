package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinates
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.GameState
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.Warmup
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class PreparationPhase(private val id: Int, private val playerId: String): Game(id) {
    private val gameState: Warmup
    val confirmed: Boolean

    private constructor(old: PreparationPhase, newGameState: GameState) {
        gameState = newGameState
        id = old.id
        playerId = old.playerId
        confirmed = false
    }

    /**
     * Confirms current fleet
     */
    private constructor(old: PreparationPhase) {
        gameState = old.gameState
        id = old.id
        playerId = old.playerId
        confirmed = true
    }

    override fun tryPlaceShip(
        ship: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): Game? {
        val newGameState = gameState.tryPlaceShip(ship, position, orientation) ?: return null
        return PreparationPhase(this, newGameState)
    }

    /**
     * Places a ship on the board.
     * @param command the command to place the ship. Format: "shipType,coordinate,orientation"
     */
    fun tryPlaceShip(command: String): Game? {
        val parsedCommand = parseCommand(command) ?: return null
        val gameResult = gameState.tryPlaceShip(parsedCommand.first, parsedCommand.second, parsedCommand.third) ?: return null
        return Game(gameResult)
    }

    /**
     * Parses a string command to place a ship.
     * @param command the command to place the ship. Format: "shipType,coordinate,orientation"
     * @return a triple with the ship type, the coordinate and the orientation
     */
    private fun parseCommand(command: String): Triple<ShipType, Coordinate, Orientation>? {
        val commandParts = command.split(",")
        if (commandParts.size != 3) return null
        val shipType = ShipType.valueOf(commandParts[0])
        val coordinate = commandParts[1].toCoordinate() ?: return null
        val orientation = Orientation.valueOf(commandParts[2])
        return Triple(shipType, coordinate, orientation)
    }

    override fun tryMoveShip(position: Coordinate, destination: Coordinate): Game? {
        val newGameResult = gameState.tryMoveShip(position, destination) ?: return null
        return Game(newGameResult)
    }

    override fun tryPlaceShot(c: Coordinate): Game? {
        return null
    }

    /**
     * Builds a new Game object, with the fleet confirmed.
     * This function will result in a new Game object, with the state changed to BATTLE.
     * From this point, it is not possible to place/move/rotate new ships.
     */
    override fun tryConfirmFleet(): Game {
        return PreparationPhase(this)
    }

    override fun tryRotateShip(position: Coordinate): Game? {
        val gameResult = gameState.tryRotateShip(position) ?: return null
        return Game(gameResult, player)
    }

    fun tryRotateShip(position: String): Game? {
        return if (gameState is Warmup) {
            val coordinate = position.toCoordinate() ?: return null
            val gameResult = gameState.tryRotateShip(coordinate) ?: return null
            Game(gameResult, player)
        } else null
    }

    fun isShip(it: Coordinate): Boolean {
        return gameState.myBoard.isShipPanel(it)
    }

    fun generateShips() : Game? {
        var game = this
        var auxGame : Game? = this
        if(game.gameState is Warmup) {
            ShipType.values().forEach {
                do{
                    auxGame = game.tryPlaceShip(
                        it,
                        Coordinates(game.gameState.configuration.boardSize).random(),
                        Orientation.random(),
                    )
                }while(auxGame == null)
                game = auxGame!!
                auxGame = null
            }
            return game
        }
        return null
    }
}
package pt.isel.daw.dawbattleshipgame.data

import pt.isel.daw.dawbattleshipgame.model.*
import pt.isel.daw.dawbattleshipgame.model.game.Game
import pt.isel.daw.dawbattleshipgame.model.ship.ShipType

class GameData {
    val myBoard: Board?
        get() = dataBase.getGame()?.board

    val opponentBoard: Board?
        get() = dataBase.getGame()?.board // TODO change to opponent board

    private val dataBase = DataBase()

    fun startNewGame(configuration: Configuration) {
        val gameResult = Game.newGame(configuration)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    fun getGameState() = dataBase.getGame()?.state

    fun placeShip(ship: ShipType, position: Coordinate, orientation: Orientation) {
        val game = dataBase.getGame()
        val gameResult = game?.tryPlaceShip(ship, position, orientation)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    fun confirmFleet() {
        val game = dataBase.getGame()
        val opponentFleet = dataBase.getOpponentBoard()
        val gameResult = game?.confirmFleet(opponentFleet)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    fun placeShot(c: Coordinate) {
        val player = getCurrentPlayer()
        val game = dataBase.getGame()
        val gameResult = game?.tryPlaceShot(c)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    private fun getCurrentPlayer(): Player {
        TODO("Not yet implemented")
    }

    fun rotateShip(position: Coordinate) {
        val game = dataBase.getGame()
        val gameResult = game?.tryRotateShip(position)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    private fun saveAndUpdateGameIfNecessary(game: Game?) {
        if (game != null)
            dataBase.saveGame(game)
    }
}
package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.data.DataBase
import pt.isel.daw.dawbattleshipgame.domain.*
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.State
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

@Component
class GameServices(private val dataBase: DataBase) {
    private fun startGame(token: String, configuration: Configuration) {
        val player = tokenToPlayer(token)
        val gameResult = Game.newGame(configuration, player)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun placeShip(token: String, ship: ShipType, position: Coordinate, orientation: Orientation) {
        val player = tokenToPlayer(token)
        val game = dataBase.getGame()
        val gameResult = game?.tryPlaceShip(ship, position, orientation)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun rotateShip(token: String, position: Coordinate) {
        val player = dataBase.getCurrentPlayer()
        val game = dataBase.getGame()
        val gameResult = game?.tryRotateShip(position)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun confirmFleet(token: String) {
        val player = tokenToPlayer(token)
        val game = dataBase.getGame()
        val opponentFleet = dataBase.getOpponentBoard()
        val gameResult = game?.confirmFleet(opponentFleet)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun placeShot(token: String, c: Coordinate) {
        val player = dataBase.getCurrentPlayer()
        val game = dataBase.getGame()
        val gameResult = game?.tryPlaceShot(c)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun getMyFleetLayout(token: String?): Board? {
        TODO("Not yet implemented")
    }

    private fun getEnemyFleetLayout(token: String?): Board? {
        TODO("Not yet implemented")
    }

    private fun getGameState(token: String?): State? {
        TODO("Not yet implemented")
    }

    private fun saveAndUpdateGameIfNecessary(token: String, game: Game?) {
        if (game != null) dataBase.saveGame(token, game)
    }

    private fun tokenToPlayer(token: String?): Player {
        TODO("Not yet implemented")
    }
}
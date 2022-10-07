package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.State
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

@Component
class GameServices(private val jdbiGamesRepository: JdbiGamesRepository) {
    private fun startGame(token: String, configuration: Configuration) {
        val player = tokenToPlayer(token)
        val gameResult = Game.newGame(configuration, player)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun placeShip(token: String, ship: ShipType, position: Coordinate, orientation: Orientation) {
        val player = tokenToPlayer(token)
        val game = jdbiGamesRepository.getGame()
        val gameResult = game?.tryPlaceShip(ship, position, orientation)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun rotateShip(token: String, position: Coordinate) {
        val player = jdbiGamesRepository.getCurrentPlayer()
        val game = jdbiGamesRepository.getGame()
        val gameResult = game?.tryRotateShip(position)
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun confirmFleet(token: String) {
        val player = tokenToPlayer(token)
        val game = jdbiGamesRepository.getGame()
        val opponentFleet = jdbiGamesRepository.getOpponentBoard()
        val gameResult = game?.tryConfirmFleet()
        saveAndUpdateGameIfNecessary(token, gameResult)
    }

    private fun placeShot(token: String, c: Coordinate) {
        val player = jdbiGamesRepository.getCurrentPlayer()
        val game = jdbiGamesRepository.getGame()
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
        if (game != null) jdbiGamesRepository.saveGame(game)
    }

    private fun tokenToPlayer(token: String?): Player {
        TODO("Not yet implemented")
    }
}
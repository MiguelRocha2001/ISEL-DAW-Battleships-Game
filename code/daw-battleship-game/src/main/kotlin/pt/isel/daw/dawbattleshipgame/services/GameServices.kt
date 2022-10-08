package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

@Component
class GameServices(private val jdbiGamesRepository: JdbiGamesRepository) {

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    private fun startGame(token: String, configuration: Configuration): Game? {
        val userWating: String = jdbiGamesRepository.getWaitingUser(configuration)
        if (userWating == null) {
            jdbiGamesRepository.joinGameQueue(token, configuration)
        } else {
            val gameId: Int = generateGameId()
            jdbiGamesRepository.removeUserFromQueue(userWating)
            val game1 = PreparationPhase(gameId, userWating)
            val game2 = PreparationPhase(gameId, token)
            jdbiGamesRepository.saveGame(game1)
            jdbiGamesRepository.saveGame(game2)
            return game2
        }
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
        val game = jdbiGamesRepository.getGame(token)
        game.tryConfirmFleet()
        saveAndUpdateGameIfNecessary(game)

        updateBothGamesIfConfirmed(game.id)
    }

    private fun updateBothGamesIfConfirmed(gameId: Int) {
        val games: Pair<PreparationPhase, PreparationPhase> = jdbiGamesRepository.getGames(gameId)
        if (games.first.confirmed && games.second.confirmed) {
            jdbiGamesRepository.removeGames(gameId) // removes both preparation pahases
            val newGamePhase = BattlePhase(gameId)
            jdbiGamesRepository.saveGame(newGamePhase)
        }
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
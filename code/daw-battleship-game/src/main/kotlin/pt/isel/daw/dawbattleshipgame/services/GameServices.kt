package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.generateGameId
import pt.isel.daw.dawbattleshipgame.repository.jdbi.Phase1
import pt.isel.daw.dawbattleshipgame.repository.jdbi.Phase2

@Component
class GameServices(private val jdbiGamesRepository: JdbiGamesRepository) {

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    private fun startGame(token: String, configuration: Configuration): Game? {
        val userWaiting: String? = jdbiGamesRepository.getWaitingUser(configuration)
        if (userWaiting == null) {
            jdbiGamesRepository.joinGameQueue(token, configuration)
        } else {
            val gameId: Int = generateGameId()
            jdbiGamesRepository.removeUserFromQueue(userWaiting)
            val game1 = PreparationPhase(gameId, configuration, userWaiting)
            val game2 = PreparationPhase(gameId, configuration, token)
            jdbiGamesRepository.saveGame(game1)
            jdbiGamesRepository.saveGame(game2)
            return game2
        }
        return null // but the user is now on the waiting queue
    }

    private fun placeShip(token: String, ship: ShipType, position: Coordinate, orientation: Orientation) {
        val game = jdbiGamesRepository.getGameByUser(token)
        val gameResult = game?.tryPlaceShip(ship, position, orientation)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    private fun rotateShip(token: String, position: Coordinate) {
        val game = jdbiGamesRepository.getGameByUser(token)
        val gameResult = game?.tryRotateShip(position)
        saveAndUpdateGameIfNecessary(gameResult)
    }

    private fun confirmFleet(token: String) {
        val game = jdbiGamesRepository.getGameByUser(token)
        game.tryConfirmFleet()
        saveAndUpdateGameIfNecessary(game)

        updateBothGamesIfConfirmed(game.gameId)
    }

    /**
     * Updates the game, in database, if both players have confirmed their fleets.
     */
    private fun updateBothGamesIfConfirmed(gameId: Int) {
        val gameFromDb = jdbiGamesRepository.getGameById(gameId)
        if (gameFromDb is Phase1) {
            val game1 = gameFromDb.gameA
            val game2 = gameFromDb.gameB
            val configuration = game1.configuration
            val player1Board = game1.board
            val player2Board = game2.board
            val player1 = game1.playerId
            val player2 = game2.playerId
            if (game1.confirmed && game2.confirmed) {
                jdbiGamesRepository.removeGame(gameId) // removes both preparation pahases
                val newGamePhase = BattlePhase(configuration, gameId, player1, player2, player1Board, player2Board)
                jdbiGamesRepository.saveGame(newGamePhase)
            }
        }
    }

    private fun placeShot(token: String, c: Coordinate) {
        val gameFromDb = jdbiGamesRepository.getGameByUser(token)
        if (gameFromDb is Phase2) {
            val game = gameFromDb.game
            if (game.isMyTurn(token)) {
                val gameResult = game.tryPlaceShot(c)
                saveAndUpdateGameIfNecessary(gameResult)
            }
        }
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

    private fun saveAndUpdateGameIfNecessary(game: Game?) {
        if (game != null) jdbiGamesRepository.saveGame(game)
    }

    private fun tokenToPlayer(token: String?): Player {
        TODO("Not yet implemented")
    }
}
package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
import pt.isel.daw.dawbattleshipgame.repository.jdbi.DbBattlePhase
import pt.isel.daw.dawbattleshipgame.repository.jdbi.DbPlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiTransaction

@Component
class GameServices(
    private val userServices: UserServices,
    private val transaction: JdbiTransaction
    ) {

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    private fun startGame(userId: Int, configuration: Configuration): PlayerPreparationPhase? {
        transaction.run {
            val db = this.gamesRepository
            val userWaiting: Int? = db.getWaitingUser(configuration)
            if (userWaiting == null) {
                db.joinGameQueue(userId, configuration)
            } else {
                val gameId: Int = generateRandomId()
                db.removeUserFromQueue(userWaiting)
                val preparationPhase = Game.newGame(gameId, userWaiting, userId, configuration)
                db.savePreparationPhase(preparationPhase)
                return preparationPhase.player2PreparationPhase
            }
            return null // but the user is now on the waiting queue
        }
    }

    private fun placeShip(token: String, ship: ShipType, position: Coordinate, orientation: Orientation) {
        transaction.run {
            val db = this.gamesRepository
            val dbGameResponse = db.getPlayerPreparationPhase(token)
            if (dbGameResponse is DbPlayerPreparationPhase) {
                val playerPreparationPhase = dbGameResponse.playerPreparationPhase
                val result = playerPreparationPhase.tryPlaceShip(ship, position, orientation) ?: return
                db.savePlayerPreparationPhase(result)
            }
        }
    }

    private fun rotateShip(token: String, position: Coordinate) {
        transaction.run {
            val db = this.gamesRepository
            val dbGameResponse = db.getPlayerPreparationPhase(token)
            if (dbGameResponse is DbPlayerPreparationPhase) {
                val playerPreparationPhase = dbGameResponse.playerPreparationPhase
                val result = playerPreparationPhase.tryRotateShip(position) ?: return
                db.savePlayerPreparationPhase(result)
            }
        }
    }

    private fun confirmFleet(token: String) {
        transaction.run {
            val db = this.gamesRepository
            val game = db.getPlayerPreparationPhase(token) ?: return
            val newGameState = game.playerPreparationPhase.confirmFleet()
            db.savePlayerWaitingPhase(newGameState)
            updateBothGamesIfConfirmed(newGameState.gameId)
        }
    }

    /**
     * Updates the game, in database, if both players have confirmed their fleets.
     */
    private fun updateBothGamesIfConfirmed(gameId: Int) {
        transaction.run {
            val db = this.gamesRepository
            val waitingPhase = db.getWaitingPhase(gameId) ?: return
            val configuration = waitingPhase.waitingPhase.configuration
            val player1 = waitingPhase.waitingPhase.player1WaitingPhase.playerId
            val player2 = waitingPhase.waitingPhase.player2WaitingPhase.playerId
            val player1Board = waitingPhase.waitingPhase.player1WaitingPhase.board
            val player2Board = waitingPhase.waitingPhase.player2WaitingPhase.board

            db.removeGame(gameId) // removes both waiting phases
            val newGamePhase = BattlePhase(configuration, gameId, player1, player2, player1Board, player2Board)
            db.saveGame(newGamePhase)
        }
    }

    private fun placeShot(userId: Int, c: Coordinate) {
        transaction.run {
            val db = this.gamesRepository
            val dbGameResponse = db.getGame()
            if (dbGameResponse is DbBattlePhase) {
                val game = dbGameResponse.game
                val result = game.tryPlaceShot(userId, c) ?: return
                saveAndUpdateGameIfNecessary(result)
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
        transaction.run {
            val db = this.gamesRepository
            if (game != null) db.saveGame(game)
        }
    }
}
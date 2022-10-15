package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId

@Component
class GameServices(
    private val userServices: UserServices,
    private val transactionManager: TransactionManager
) {

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    private fun startGame(userId: Int, configuration: Configuration): PlayerPreparationPhase? {
        return transactionManager.run {
            val db = it.gamesRepository
            val userWaiting: Int? = db.getWaitingUser(configuration)
            if (userWaiting == null) {
                db.joinGameQueue(userId, configuration)
                null
            } else {
                val gameId = generateRandomId()
                db.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(gameId, userWaiting, userId, configuration)
                db.savePreparationPhase(newGame)
                newGame.player1Game as PlayerPreparationPhase
            }
        }
    }

    private fun placeShip(userId: Int, ship: ShipType, position: Coordinate, orientation: Orientation) {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: throw Exception("User not in a game")
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryPlaceShip(ship, position, orientation) ?: throw Exception("Invalid ship placement")
                    val newGame = game.copy(player1Game = newPlayerPreparationPhase)
                    db.savePreparationPhase(newGame)
                } else {
                    throw Exception("User not in preparation phase")
                }
            }
        }
    }

    private fun rotateShip(userId: Int, position: Coordinate) {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: throw Exception("User not in a game")
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryRotateShip(position) ?: throw Exception("Invalid ship placement")
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    db.savePreparationPhase(newGame)
                } else {
                    throw Exception("User not in preparation phase")
                }
            }
        }
    }

    private fun confirmFleet(userId: Int) {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: throw Exception("User not in a game")
            if (game is SinglePhase) {
                val player1Game = game.player1Game
                val player2Game = game.player2Game
                val newGame = if (game.player1 == userId) {
                    if (player1Game is PlayerPreparationPhase) {
                        if (player2Game is PlayerWaitingPhase) {
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
                        } else game.copy(player1Game = player1Game.confirmFleet())
                    } else throw Exception("User not in preparation phase")
                } else {
                    if (player2Game is PlayerPreparationPhase) {
                        if (player1Game is PlayerWaitingPhase) {
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
                        } else game.copy(player2Game = player2Game.confirmFleet())
                    } else throw Exception("User not in preparation phase")
                }
                db.saveGame(newGame)
            }
        }
    }

    private fun placeShot(userId: Int, c: Coordinate) {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: throw Exception("User not in a game")
            if (game is BattlePhase) {
                val result = game.tryPlaceShot(userId, c)
                if (result != null)
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
        transactionManager.run {
            val db = it.gamesRepository
            if (game != null) db.saveGame(game)
        }
    }
}
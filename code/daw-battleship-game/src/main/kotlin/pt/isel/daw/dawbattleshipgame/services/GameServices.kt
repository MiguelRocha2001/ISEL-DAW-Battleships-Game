package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.state.*
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId

// TODO -> define representations to return in functions bellow

@Component
class GameServices(
    private val transactionManager: TransactionManager
) {

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    fun startGame(userId: Int, configuration: Configuration): PlayerPreparationPhase? {
        return transactionManager.run {
            val gameDb = it.gamesRepository
            val userDb = it.usersRepository
            if (userDb.isAlreadyInQueue(userId)) {
                return@run null
            }
            val userWaiting = userDb.getFirstUserInQueue()
            if (userWaiting == null) {
                gameDb.joinGameQueue(userId, configuration)
                null
            } else {
                val gameId = generateRandomId()
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(gameId, userWaiting, userId, configuration)
                gameDb.savePreparationPhase(newGame)
                newGame.player1Game as PlayerPreparationPhase
            }
        }
    }

    fun getGame(gameId: Int): Game? {
        return transactionManager.run {
            it.gamesRepository.getGame(gameId)
        }
    }


    fun placeShip(userId: Int, ship: ShipType, position: Coordinate, orientation: Orientation) {
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

    fun rotateShip(userId: Int, position: Coordinate) {
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

    fun confirmFleet(userId: Int) {
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

    fun placeShot(userId: Int, c: Coordinate) {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: throw Exception("User not in a game")
            if (game is BattlePhase) {
                val result = game.tryPlaceShot(userId, c)
                if (result != null)
                    db.saveGame(game)
            }
        }
    }

    fun getMyFleetLayout(token: String?): Board? {
        TODO("Not yet implemented")
    }

    fun getEnemyFleetLayout(token: String?): Board? {
        TODO("Not yet implemented")
    }

    fun getGameState(token: String?): State? {
        TODO("Not yet implemented")
    }
}
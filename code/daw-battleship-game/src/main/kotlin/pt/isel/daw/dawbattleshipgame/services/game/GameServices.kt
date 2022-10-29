package pt.isel.daw.dawbattleshipgame.services.game

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.*
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId

@Component
class GameServices(
    private val transactionManager: TransactionManager
) {
    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    fun startGame(userId: Int, configuration: Configuration): GameCreationResult {
        return transactionManager.run {
            val gameDb = it.gamesRepository
            val userDb = it.usersRepository
            if (userDb.isAlreadyInQueue(userId)) {
                return@run Either.Left(GameCreationError.UserAlreadyInQueue)
            }
            val userWaiting = userDb.getFirstUserInQueue()
            if (userWaiting == null) {
                userDb.insertInGameQueue(userId)
                Either.Right(GameState.NOT_STARTED to null)
            } else {
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(generateRandomId(), userWaiting, userId, configuration)
                gameDb.saveGame(newGame)
                Either.Right(newGame.state to newGame.gameId)
            }
        }
    }

    fun getGameIdByUser(userId: Int): GameIdResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameIdError.GameNotFound)
            Either.Right(game.gameId)
        }
    }

    fun placeShip(userId: Int, ship: ShipType, position: Coordinate, orientation: Orientation): PlaceShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShipError.GameNotFound)
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryPlaceShip(ship, position, orientation)
                        ?: return@run Either.Left(PlaceShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    replaceGame(db, newGame)
                    return@run Either.Right(newGame.state)
                }
            }
            Either.Left(PlaceShipError.ActionNotPermitted)
        }
    }

    fun rotateShip(userId: Int, position: Coordinate): RotateShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(RotateShipError.GameNotFound)
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryRotateShip(position)
                        ?: return@run Either.Left(RotateShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    replaceGame(db, newGame)
                    return@run Either.Right(newGame.state)
                }
            }
            Either.Left(RotateShipError.ActionNotPermitted)
        }
    }

    fun moveShip(userId: Int, origin: Coordinate, destination: Coordinate): MoveShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(MoveShipError.GameNotFound)
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryMoveShip(origin, destination)
                        ?: return@run Either.Left(MoveShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    replaceGame(db, newGame)
                    return@run Either.Right(newGame.state)
                }
            }
            Either.Left(MoveShipError.ActionNotPermitted)
        }
    }

    fun confirmFleet(userId: Int): FleetConfirmationResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(FleetConfirmationError.GameNotFound)
            if (game is SinglePhase) {
                val player1Game = game.player1Game
                val player2Game = game.player2Game
                val newGame = if (game.player1 == userId) {
                    if (player1Game.isNotWaiting()) {
                        if (player2Game.isWaiting()) {
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
                        } else game.copy(player1Game = player1Game.logic.confirmFleet())
                    } else return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
                } else {
                    if (player2Game.isNotWaiting()) {
                        if (player1Game.isWaiting()) {
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
                        } else game.copy(player2Game = player2Game.logic.confirmFleet())
                    } else throw Exception("User not in preparation phase")
                }
                replaceGame(db, newGame)
                Either.Right(newGame.state)
            } else {
                Either.Left(FleetConfirmationError.ActionNotPermitted)
            }
        }
    }

    fun placeShot(userId: Int, c: Coordinate): PlaceShotResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShotError.GameNotFound)
            if (game is BattlePhase) {
                val newGame = game.tryPlaceShot(userId, c) ?: return@run Either.Left(PlaceShotError.InvalidMove)
                replaceGame(db, newGame)
                return@run Either.Right(newGame.state)
            }
            Either.Left(PlaceShotError.ActionNotPermitted)
        }
    }

    fun getMyFleetLayout(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            when (game) {
                is SinglePhase -> {
                    if (game.player1 == userId) Either.Right(game.player1Game.board)
                    else Either.Right(game.player2Game.board)
                }
                is BattlePhase -> {
                    if (game.player1 == userId) Either.Right(game.board1)
                    else Either.Right(game.board2)
                }
                is FinishedPhase -> {
                    if (game.player1 == userId) Either.Right(game.board1)
                    else Either.Right(game.board2)
                }
                else -> TODO()
            }
        }
    }

    fun getOpponentFleet(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            return@run when (game) {
                is SinglePhase -> {
                    if (game.player1 == userId) Either.Right(game.player2Game.board)
                    else Either.Right(game.player1Game.board)
                }
                is BattlePhase -> {
                    if (game.player1 == userId) Either.Right(game.board2)
                    else Either.Right(game.board1)
                }
                is FinishedPhase -> {
                    if (game.player1 == userId) Either.Right(game.board1)
                    else Either.Right(game.board1)
                }
                else -> TODO()
            }
        }
    }

    fun getGameState(userId: Int): GameStateResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameStateError.GameNotFound)
            return@run when (game) {
                is SinglePhase -> {
                    if (game.player1 == userId) {
                        if (game.player1Game.isNotWaiting()) {
                            Either.Right(GameState.FLEET_SETUP)
                        } else {
                            Either.Right(GameState.WAITING)
                        }
                    } else {
                        if (game.player2Game.isNotWaiting()) {
                            Either.Right(GameState.FLEET_SETUP)
                        } else {
                            Either.Right(GameState.WAITING)
                        }
                    }
                }
                is BattlePhase -> {
                    Either.Right(GameState.BATTLE)
                }
                is FinishedPhase -> {
                    Either.Right(GameState.FINISHED)
                }
                else -> TODO()
            }
        }
    }

    fun getGame(gameId: Int): GameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameError.GameNotFound)
            Either.Right(game)
        }
    }

    private fun replaceGame(db: GamesRepository, game: Game) {
        db.removeGame(game.gameId)
        db.saveGame(game)
    }
}
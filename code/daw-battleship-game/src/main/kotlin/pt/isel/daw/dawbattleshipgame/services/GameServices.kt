package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.state.*
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId

sealed class GameCreationError {
    object UserAlreadyInGame : GameCreationError()
    object UserAlreadyInQueue : GameCreationError()
}
typealias GameCreationResult = Either<GameCreationError, Unit>

sealed class PlaceShipError {
    object UserNotInGame : PlaceShipError()
    object ActionNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias PlaceShipResult = Either<PlaceShipError, Unit>

sealed class MoveShipError {
    object UserNotInGame : MoveShipError()
    object ActionNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias MoveShipResult = Either<MoveShipError, Unit>

sealed class RotateShipError {
    object UserNotInGame : RotateShipError()
    object ActionNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias RotateShipResult = Either<RotateShipError, Unit>

sealed class FleetConfirmationError {
    object UserNotInGame : FleetConfirmationError()
    object ActionNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias FleetConfirmationResult = Either<FleetConfirmationError, Unit>

sealed class PlaceShotError {
    object UserNotInGame : PlaceShotError()
    object MoveNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias PlaceShotResult = Either<PlaceShotError, Unit>

sealed class GameSearchError {
    object UserNotInGame : GameSearchError()
    object GameNotFound : GameSearchError()
}
typealias GameSearchResult = Either<GameSearchError, Board>

sealed class GameStateError {
    object UserNotInGame : GameStateError()
    object GameNotFound : GameStateError()
}
typealias GameStateResult = Either<GameStateError, String>


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
                gameDb.joinGameQueue(userId, configuration)
                return@run Either.Right(Unit)
            } else {
                val gameId = generateRandomId()
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(gameId, userWaiting, userId, configuration)
                gameDb.savePreparationPhase(newGame)
                newGame.player1Game as PlayerPreparationPhase
                return@run Either.Right(Unit)
            }
        }
    }

    fun placeShip(userId: Int, ship: ShipType, position: Coordinate, orientation: Orientation): PlaceShipResult {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: Either.Left()
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryPlaceShip(ship, position, orientation)
                        ?: return@run Either.Left(PlaceShipError.InvalidMove)
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
            val game = db.getGameByUser(userId) ?: return@run Either.Left()
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryRotateShip(position)
                        ?: return@run Either.Left(RotateShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    db.savePreparationPhase(newGame)
                    return@run Either.Right(Unit)
                } else {
                    return@run Either.Left(MoveShipError.ActionNotPermitted)
                }
            }
        }
    }

    fun moveShip(userId: Int, origin: Coordinate, destination: Coordinate): MoveShipResult {
        transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: Either.Left(GameNotFound.GameNotFound)
            if (game is SinglePhase) {
                val playerGame = if (game.player1 == userId) game.player1Game else game.player2Game
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryMoveShip(origin, destination)
                        ?: return@run Either.Left(MoveShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    db.savePreparationPhase(newGame)
                    return@run Either.Right(Unit)
                } else {
                    return@run Either.Left(MoveShipError.ActionNotPermitted)
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
            } else {
                return@run Either.Left(MoveShipError.ActionNotPermitted)
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

    fun getMyFleetLayout(userId: Int): GameSearchResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameNotFound.GameNotFound)
            return@run when (game) {
                is SinglePhase -> {
                    if (game.player1 == userId) Either.Right(game.player1Game.board)
                    else Either.Right(game.player2Game.board)
                }
                is BattlePhase -> {
                    if (game.player1 == userId) Either.Right(game.player1Board)
                    else Either.Right(game.player2Board)
                }
                is EndPhase -> {
                    if (game.player1 == userId) Either.Right(game.player1Board)
                    else Either.Right(game.player2Board)
                }
            }
        }
    }

    fun getOpponentFleet(userId: Int): GameSearchResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameNotFound.GameNotFound)
            return@run when (game) {
                is SinglePhase -> {
                    if (game.player1 == userId) Either.Right(game.player2Game.board)
                    else Either.Right(game.player1Game.board)
                }
                is BattlePhase -> {
                    if (game.player1 == userId) Either.Right(game.player2Board)
                    else Either.Right(game.player1Board)
                }
                is EndPhase -> {
                    if (game.player1 == userId) Either.Right(game.player2Board)
                    else Either.Right(game.player1Board)
                }
            }
        }
    }

    fun getGameState(userId: Int): GameStateResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameStateError.GameNotFound)
            return@run when (game) {
                is SinglePhase -> {
                    Either.Right("Preparation Phase")
                }
                is BattlePhase -> {
                    Either.Right("Battle Phase")
                }
                is EndPhase -> {
                    Either.Right("Finished Phase")
                }
            }
        }
    }
}
package pt.isel.daw.dawbattleshipgame.services.game

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.state.*
<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
=======
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase
>>>>>>> Stashed changes
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
                Either.Left(GameCreationError.UserAlreadyInQueue)
            }
            val userWaiting = userDb.getFirstUserInQueue()
            if (userWaiting == null) {
                userDb.insertInGameQueue(userId)
                Either.Right(Unit)
            } else {
<<<<<<< Updated upstream
                val gameId = generateRandomId()
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(gameId, userWaiting, userId, configuration)
=======
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.newGame(userWaiting, userId, 1, configuration)
>>>>>>> Stashed changes
                gameDb.saveGame(newGame)
                Either.Right(Unit)
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
<<<<<<< Updated upstream
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryPlaceShip(ship, position, orientation)
=======
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryPlaceShip(ship, position, orientation)
>>>>>>> Stashed changes
                        ?: return@run Either.Left(PlaceShipError.InvalidMove)
                    val newGame = game.copy(player1Game = newPlayerPreparationPhase)
                    replaceGame(newGame)
                    Either.Right(Unit)
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
<<<<<<< Updated upstream
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryRotateShip(position)
=======
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryRotateShip(position)
>>>>>>> Stashed changes
                        ?: return@run Either.Left(RotateShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    replaceGame(newGame)
                    Either.Right(Unit)
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
<<<<<<< Updated upstream
                if (playerGame is PlayerPreparationPhase) {
                    val newPlayerPreparationPhase = playerGame.tryMoveShip(origin, destination)
=======
                if (playerGame.isNotWaiting()) {
                    val newPlayerPreparationPhase = playerGame.logic.tryMoveShip(origin, destination)
>>>>>>> Stashed changes
                        ?: return@run Either.Left(MoveShipError.InvalidMove)
                    val newGame = if (game.player1 == userId) game.copy(player1Game = newPlayerPreparationPhase)
                    else game.copy(player2Game = newPlayerPreparationPhase)
                    replaceGame(newGame)
                    Either.Right(Unit)
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
<<<<<<< Updated upstream
                    if (player1Game is PlayerPreparationPhase) {
                        if (player2Game is PlayerWaitingPhase) {
=======
                    if (player1Game.isNotWaiting()) {
                        if (player2Game.isWaiting()) {
>>>>>>> Stashed changes
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
<<<<<<< Updated upstream
                        } else game.copy(player1Game = player1Game.confirmFleet())
                    } else return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
                } else {
                    if (player2Game is PlayerPreparationPhase) {
                        if (player1Game is PlayerWaitingPhase) {
=======
                        } else game.copy(player1Game = player1Game.logic.confirmFleet())
                    } else return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
                } else {
                    if (player2Game.isNotWaiting()) {
                        if (player1Game.isWaiting()) {
>>>>>>> Stashed changes
                            BattlePhase(
                                game.configuration,
                                game.gameId,
                                game.player1,
                                game.player2,
                                player1Game.board,
                                player2Game.board
                            )
<<<<<<< Updated upstream
                        } else game.copy(player2Game = player2Game.confirmFleet())
=======
                        } else game.copy(player2Game = player2Game.logic.confirmFleet())
>>>>>>> Stashed changes
                    } else throw Exception("User not in preparation phase")
                }
                replaceGame(newGame)
                Either.Right(Unit)
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
                val result = game.tryPlaceShot(userId, c) ?: return@run Either.Left(PlaceShotError.InvalidMove)
                replaceGame(result)
                Either.Right(Unit)
            }
            Either.Left(PlaceShotError.ActionNotPermitted)
        }
    }

    fun getMyFleetLayout(userId: Int): GameSearchResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            when (game) {
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
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
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
                    if (game.player1 == userId) {
<<<<<<< Updated upstream
                        if (game.player1Game is PlayerPreparationPhase) {
=======
                        if (game.player1Game.isNotWaiting()) {
>>>>>>> Stashed changes
                            Either.Right(GameState.FLEET_SETUP)
                        } else {
                            Either.Right(GameState.WAITING)
                        }
                    } else {
<<<<<<< Updated upstream
                        if (game.player2Game is PlayerPreparationPhase) {
=======
                        if (game.player2Game.isNotWaiting()) {
>>>>>>> Stashed changes
                            Either.Right(GameState.FLEET_SETUP)
                        } else {
                            Either.Right(GameState.WAITING)
                        }
                    }
                }
                is BattlePhase -> {
                    Either.Right(GameState.BATTLE)
                }
                is EndPhase -> {
                    Either.Right(GameState.FINISHED)
                }
            }
        }
    }

<<<<<<< Updated upstream
    fun getGame(gameId: Int) {
        TODO()
    }

=======
>>>>>>> Stashed changes
    private fun replaceGame(game: Game) {
        return transactionManager.run {
            val db = it.gamesRepository
            db.removeGame(game.gameId)
            db.saveGame(game)
        }
    }
}
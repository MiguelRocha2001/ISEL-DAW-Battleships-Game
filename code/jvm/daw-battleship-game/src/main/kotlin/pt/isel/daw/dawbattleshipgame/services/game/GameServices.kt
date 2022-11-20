package pt.isel.daw.dawbattleshipgame.services.game

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager

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
            if (gameDb.isInGame(userId)) {
                return@run Either.Left(GameCreationError.UserAlreadyInGame)
            }
            val userWaiting = userDb.getFirstUserInQueue()
            if (userWaiting == null) {
                userDb.insertInGameQueue(userId)
                Either.Right(GameState.NOT_STARTED to null)
            } else {
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.startGame(userWaiting, userId, configuration)
                val gameId = gameDb.startGame(newGame)
                gameId ?: Either.Left(GameCreationError.GameNotFound)
                Either.Right(GameState.FLEET_SETUP to gameId)
            }
        }
    }

    fun getGameIdByUser(userId: Int): GameIdResult {
        return transactionManager.run {
            val gamesDb = it.gamesRepository
            val usersDb = it.usersRepository
            if (usersDb.isInQueue(userId)) {
                return@run Either.Left(GameIdError.UserInGameQueue)
            }
            val game = gamesDb.getGameByUser(userId) ?:
                return@run Either.Left(GameIdError.GameNotFound)
            Either.Right(game.id)
        }
    }

    /**
     * @param gameId the game's id, or null if game is the current user game
     */
    fun placeShips(userId: Int, ships: List<Triple<ShipType, Coordinate, Orientation>>): PlaceShipsResult {
        return transactionManager.run {
            val db = it.gamesRepository
            var game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShipsError.GameNotFound)
            val player = game.getUser(userId)
            if (game.state != GameState.FLEET_SETUP)
                return@run Either.Left(PlaceShipsError.ActionNotPermitted)
            ships.forEach { s ->
                game = game.placeShip(s.first, s.second, s.third, player)
                    ?: return@run Either.Left(PlaceShipsError.InvalidMove)
            }
            updateGame(db, game)
            return@run Either.Right(Unit)
        }
    }

    fun updateShip(userId: Int, position: Coordinate, newCoordinate: Coordinate? = null): UpdateShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(UpdateShipError.GameNotFound)
            val player = game.getUser(userId)
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(UpdateShipError.ActionNotPermitted)
            val newGame = if(newCoordinate != null)
                        game.moveShip(position, newCoordinate, player)
                            else game.rotateShip(position, player)
            if(newGame == null)
                return@run Either.Left(UpdateShipError.InvalidMove)
            updateGame(db, newGame)
            return@run Either.Right(Unit)
        }
    }

    fun updateFleetState(userId: Int, confirmed: Boolean): FleetConfirmationResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(FleetConfirmationError.GameNotFound)
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            val player = game.getUser(userId)
            if(game.getBoard(player).isConfirmed())
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            if (!confirmed) return@run Either.Right(Unit)
            val newGame = game.confirmFleet(player)
            updateGame(db, newGame)
            Either.Right(Unit)
        }
    }

    /**
     * @param gameId the game's id, or null if game is the current user game
     */
    fun placeShot(userId: Int, c: Coordinate): PlaceShotResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShotError.GameNotFound)
            if(game.state != GameState.BATTLE)
                return@run Either.Left(PlaceShotError.ActionNotPermitted)
            val newGame = game.placeShot(userId, c, game.getUser(userId))
                ?: return@run Either.Left(PlaceShotError.InvalidMove)
            updateGame(db, newGame)
            return@run Either.Right(Unit)
        }
    }

    fun getMyFleetLayout(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            return@run Either.Right(game.getBoard(game.getUser(userId)))
        }
    }

    fun getOpponentFleet(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            val opponent = game.getUser(userId).other()
            return@run Either.Right(game.getBoard(opponent))
        }
    }

    fun getGameState(gameId: Int): GameStateResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameStateError.GameNotFound)
            return@run Either.Right(game.state)
        }
    }

    fun getGame(gameId: Int): GameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameError.GameNotFound)
            Either.Right(game)
        }
    }

    fun getGameByUser(userId: Int): GameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameError.GameNotFound)
            Either.Right(game)
        }
    }

    @Deprecated("Use update game instead")
    private fun replaceGame(db: GamesRepository, game: Game) {
        db.removeGame(game.id)
        db.saveGame(game)
    }

    private fun updateGame(db: GamesRepository, game: Game){
        db.updateGame(game)
    }

    fun deleteGame(gameId: Int): DeleteGameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            db.getGame(gameId) ?: return@run Either.Left(DeleteGameError.GameNotFound)
            db.removeGame(gameId)
            return@run Either.Right(Unit)
        }
    }
}
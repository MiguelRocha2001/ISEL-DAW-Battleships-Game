package pt.isel.daw.dawbattleshipgame.services.game

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger("GameServices")

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     */
    fun startGame(userId: Int, configuration: Configuration): GameCreationResult {
        return transactionManager.run {
            val gameDb = it.gamesRepository
            val userDb = it.usersRepository
            if (userDb.isAlreadyInQueue(userId)) {
                logger.info("Game creation failed: user is already in queue")
                return@run Either.Left(GameCreationError.UserAlreadyInQueue)
            }
            if (gameDb.isInGame(userId)) {
                logger.info("Game creation failed: user is already in game")
                return@run Either.Left(GameCreationError.UserAlreadyInGame)
            }
            val userWaiting = userDb.getFirstUserInQueue()
            if (userWaiting == null) {
                userDb.insertInGameQueue(userId)
                logger.info("Game creation successful: user is now in queue")
                Either.Right(GameState.NOT_STARTED to null)
            } else {
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.startGame(userWaiting, userId, configuration)
                val gameId = gameDb.startGame(newGame)
                gameId ?: Either.Left(GameCreationError.GameNotFound)
                    .also { logger.info("Game creation failed: game not found") }
                logger.info("Game creation successful: game started")
                Either.Right(GameState.FLEET_SETUP to gameId)
            }
        }
    }

    fun getGameIdByUser(userId: Int): GameIdResult {
        return transactionManager.run {
            val gamesDb = it.gamesRepository
            val usersDb = it.usersRepository
            if (usersDb.isInQueue(userId)) {
                logger.info("Game id retrieval failed: user is in queue")
                return@run Either.Left(GameIdError.UserInGameQueue)
            }
            val game = gamesDb.getGameByUser(userId) ?:
                return@run Either.Left(GameIdError.GameNotFound)
                    .also { logger.info("Game id retrieval failed: game not found") }
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
                .also { logger.info("Place ships failed: game not found") }
            val player = game.getUser(userId)
            if (game.state != GameState.FLEET_SETUP)
                return@run Either.Left(PlaceShipsError.ActionNotPermitted)
                    .also { logger.info("Place ships failed: action not permitted") }
            ships.forEach { s ->
                game = game.placeShip(s.first, s.second, s.third, player)
                    ?: return@run Either.Left(PlaceShipsError.InvalidMove)
                        .also { logger.info("Ship placement failed: invalid move") }
            }
            updateGame(db, game)
            logger.info("Ship placement successful")
            return@run Either.Right(Unit)
        }
    }

    fun updateShip(userId: Int, position: Coordinate, newCoordinate: Coordinate? = null): UpdateShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(UpdateShipError.GameNotFound)
                .also { logger.info("Update ship failed: game not found") }
            val player = game.getUser(userId)
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(UpdateShipError.ActionNotPermitted)
                    .also { logger.info("Ship update failed: action not permitted") }
            val newGame =
                if(newCoordinate != null) game.moveShip(position, newCoordinate, player)
                else game.rotateShip(position, player)
            if(newGame == null) {
                logger.info("Ship update failed: invalid move")
                return@run Either.Left(UpdateShipError.ActionNotPermitted)
            }
            updateGame(db, newGame)
            logger.info("Ship update successful")
            return@run Either.Right(Unit)
        }
    }

    fun updateFleetState(userId: Int, confirmed: Boolean): FleetConfirmationResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(FleetConfirmationError.GameNotFound)
                .also { logger.info("Fleet confirmation failed: game not found") }
            if(game.state != GameState.FLEET_SETUP) {
                logger.info("Fleet confirmation failed: action not permitted")
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            }
            val player = game.getUser(userId)
            if(game.getBoard(player).isConfirmed()) {
                logger.info("Fleet confirmation failed: fleet already confirmed")
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            }
            if (!confirmed) return@run Either.Right(Unit).also { logger.info("Fleet confirmation successful") }
            val newGame = game.confirmFleet(player) ?: return@run Either.Left(FleetConfirmationError.NotAllShipsPlaced)
                .also { logger.info("Fleet confirmation failed: not all ships placed") }
            updateGame(db, newGame)
            logger.info("Fleet confirmation successful")
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
                .also { logger.info("Place shot failed: game not found") }
            if(game.state != GameState.BATTLE) {
                logger.info("Place shot failed: action not permitted")
                return@run Either.Left(PlaceShotError.ActionNotPermitted)
            }
            val newGame = game.placeShot(userId, c, game.getUser(userId))
                ?: return@run Either.Left(PlaceShotError.InvalidMove)
                    .also { logger.info("Place shot failed: invalid move") }
            updateGame(db, newGame)
            logger.info("Place shot successful")
            return@run Either.Right(Unit)
        }
    }

    fun getMyFleetLayout(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
                .also { logger.info("Get my fleet layout failed: game not found") }
            logger.info("Get my fleet layout successful")
            return@run Either.Right(game.getBoard(game.getUser(userId)))
        }
    }

    fun getOpponentFleet(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
                .also { logger.info("Get opponent fleet failed: game not found") }
            val opponent = game.getUser(userId).other()
            logger.info("Get opponent fleet successful")
            return@run Either.Right(game.getBoard(opponent))
        }
    }

    fun getGameState(gameId: Int): GameStateResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameStateError.GameNotFound)
                .also { logger.info("Get game state failed: game not found") }
            logger.info("Get game state successful")
            return@run Either.Right(game.state)
        }
    }

    fun getGame(gameId: Int): GameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameError.GameNotFound)
                .also { logger.info("Get game failed: game not found") }
            logger.info("Get game successful")
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
                .also { logger.info("Delete game failed: game not found") }
            db.removeGame(gameId)
            logger.info("Delete game successful")
            return@run Either.Right(Unit)
        }
    }
}
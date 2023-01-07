package pt.isel.daw.dawbattleshipgame.services.game

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager

@Component
class GameServices(
    private val transactionManager: TransactionManager
) {
    private val logger: Logger = LoggerFactory.getLogger("GameServices")

    fun isInWaitingRoom(userId: Int): Boolean {
        return transactionManager.run {
            val userDb = it.usersRepository
            if (userDb.isAlreadyInQueue(userId)) return@run true
            return@run false
        }
    }

    /**
     * Initiates a new game with some other user, awaiting. If there's none,
     * joins a queue and waits for another user to join.
     * When [configuration] is null starts a quick game with the first player in queue
     * If there is no one in queue adds the player to queue with [Configuration.DEFAULT]
     */
    fun startGame(userId: Int, configuration: Configuration?): GameCreationResult {
        val quickGame = configuration == null
        return transactionManager.run {
            val gameDb = it.gamesRepository
            val userDb = it.usersRepository
            if (userDb.isAlreadyInQueue(userId)) {
                logger.info("User $userId: Game creation failed: user is already in queue")
                return@run Either.Left(GameCreationError.UserAlreadyInQueue)
            }
            if (gameDb.isInGame(userId)) {
                logger.info("User $userId: Game creation failed: user is already in game")
                return@run Either.Left(GameCreationError.UserAlreadyInGame)
            }

            val userWaiting = if(quickGame) userDb.getFirstUserInQueue()
            else userDb.getFirstUserWithSameConfigInQueue(configuration!!) //safe double bang

            if (userWaiting == null) {
                userDb.insertInGameQueue(userId,configuration ?: Configuration.DEFAULT)
                logger.info("User $userId: Game creation successful: user is now in queue")
                Either.Right(GameState.NOT_STARTED to null)
            } else {
                val conf =
                    if(quickGame) userDb.getConfigFromUserQueue(userWaiting)
                    else configuration
                userDb.removeUserFromQueue(userWaiting)
                val newGame = Game.startGame(userWaiting, userId, conf!!)
                val gameId = gameDb.startGame(newGame)
                gameId ?: Either.Left(GameCreationError.GameNotFound)
                    .also { logger.info("User $userId: Game creation failed: game not found") }
                logger.info("User $userId: Game creation successful: game started")
                Either.Right(GameState.FLEET_SETUP to gameId)
            }
        }
    }

    fun getGameIdByUser(userId: Int): GameIdResult {
        return transactionManager.run {
            val gamesDb = it.gamesRepository
            val usersDb = it.usersRepository
            if (usersDb.isInQueue(userId)) {
                logger.info("User $userId: Game id retrieval failed: user is in queue")
                return@run Either.Left(GameIdError.UserInGameQueue)
            }
            val game = gamesDb.getGameByUser(userId) ?:
                return@run Either.Left(GameIdError.GameNotFound)
                    .also { logger.info("User $userId: Game id retrieval failed: game not found") }
            Either.Right(game.id)
                .also { logger.info("User $userId: Game id successfully") }
        }
    }

    /**
     * If user is in a game quit game by finishing and giving the win to the other player
     */
    fun quitGame(userId : Int, gameId: Int) : GameQuitResult {
        return transactionManager.run {
            val gamesDb = it.gamesRepository
            val usersDb = it.usersRepository
            if (usersDb.isInQueue(userId)) {
                logger.info("User $userId: Game quit failed: user is in queue")
                return@run Either.Left(GameQuitError.UserInGameQueue)
            }
            val game = gamesDb.getGame(gameId) ?: return@run Either.Left(GameQuitError.GameNotFound)
                .also { logger.info("User $userId: Game quit failed: game not found") }
            val newGame = game.setWinner(game.otherPlayer(userId))
            gamesDb.updateGame(newGame)
            Either.Right(newGame.id)
                .also { logger.info("User $userId: Game quit successful") }
        }
    }


    /**
     * @param gameId the game's id, or null if game is the current user game
     */
    fun placeShips(
            userId: Int,
            ships: List<Triple<ShipType, Coordinate, Orientation>>,
            confirmFleet: Boolean = false
    ): PlaceShipsResult {
        return transactionManager.run {
            val db = it.gamesRepository
            var game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShipsError.GameNotFound)
                .also { logger.info("User $userId: Place ships failed: game not found") }
            val player = game.getUser(userId)
            if (game.state != GameState.FLEET_SETUP)
                return@run Either.Left(PlaceShipsError.ActionNotPermitted)
                    .also { logger.info("User $userId: Place ships failed: action not permitted") }
            if(game.getBoard(player).isConfirmed())
                return@run Either.Left(PlaceShipsError.BoardIsConfirmed)
            ships.forEach { s ->
                game = game.placeShip(s.first, s.second, s.third, player)
                    ?: return@run Either.Left(PlaceShipsError.InvalidMove)
                        .also { logger.info("User $userId: Ship placement failed: invalid move") }
            }
            if (confirmFleet) {
                game = game.confirmFleet(player)
                    ?: return@run Either.Left(PlaceShipsError.ActionNotPermitted)
                        .also { logger.info("User $userId: Ship placement failed: action not permitted") }
            }
            updateGame(db, game)
            logger.info("User $userId: Ship placement successful")
            return@run Either.Right(Unit)
        }
    }

    fun updateShip(userId: Int, position: Coordinate, newCoordinate: Coordinate? = null): UpdateShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(UpdateShipError.GameNotFound)
                .also { logger.info("User $userId: Update ship failed: game not found") }
            val player = game.getUser(userId)
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(UpdateShipError.ActionNotPermitted)
                    .also { logger.info("User $userId: Ship update failed: action not permitted") }
            val newGame =
                if(newCoordinate != null) game.moveShip(position, newCoordinate, player)
                else game.rotateShip(position, player)
            if(newGame == null) {
                logger.info("User $userId: Ship update failed: invalid move")
                return@run Either.Left(UpdateShipError.ActionNotPermitted)
            }
            updateGame(db, newGame)
            logger.info("User $userId: Ship update successful")
            return@run Either.Right(Unit)
        }
    }

    fun updateFleetState(userId: Int, confirmed: Boolean): FleetConfirmationResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(FleetConfirmationError.GameNotFound)
                .also { logger.info("User $userId: Fleet confirmation failed: game not found") }
            if(game.state != GameState.FLEET_SETUP) {
                logger.info("User $userId: Fleet confirmation failed: action not permitted")
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            }
            val player = game.getUser(userId)
            if(game.getBoard(player).isConfirmed()) {
                logger.info("User $userId: Fleet confirmation failed: fleet already confirmed")
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            }
            if (!confirmed) return@run Either.Right(Unit).also { logger.info("Fleet confirmation successful") }
            val newGame = game.confirmFleet(player) ?: return@run Either.Left(FleetConfirmationError.NotAllShipsPlaced)
                .also { logger.info("User $userId: Fleet confirmation failed: not all ships placed") }
            updateGame(db, newGame)
            logger.info("User $userId: Fleet confirmation successful")
            Either.Right(Unit)
        }
    }

    /**
     * @param gameId the game's id, or null if game is the current user game
     */
    fun placeShots(userId: Int, c: List<Coordinate>): PlaceShotResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(PlaceShotError.GameNotFound)
                .also { logger.info("User $userId: Place shot failed: game not found") }

            if(game.state != GameState.BATTLE || game.playerTurn != userId) {
                logger.info("User $userId: Place shot failed: action not permitted")
                return@run Either.Left(PlaceShotError.ActionNotPermitted)
            }

            if(c.isEmpty()) return@run Either.Left(PlaceShotError.EmptyShotsList)

            val newGame = game.placeShots(userId, c, game.getUser(userId))
                ?: return@run Either.Left(PlaceShotError.InvalidShot)
                    .also { logger.info("User $userId: Place shot failed: invalid shot") }

            updateGame(db, newGame)
            logger.info("User $userId: Place shot successful")
            return@run Either.Right(Unit)
        }
    }

    fun getMyFleetLayout(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
                .also { logger.info("User $userId: Get my fleet layout failed: game not found") }
            logger.info("User $userId: Get my fleet layout successful")
            return@run Either.Right(game.getBoard(game.getUser(userId)))
        }
    }

    fun getOpponentFleet(userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameSearchError.GameNotFound)
                .also { logger.info("User $userId: Get opponent fleet failed: game not found") }
            val opponent = game.getUser(userId).other()
            logger.info("User $userId: Get opponent fleet successful")
            return@run Either.Right(game.getBoard(opponent))
        }
    }

    fun getGameState(gameId: Int): GameStateResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameStateError.GameNotFound)
                .also { logger.info("Game $gameId: Get game state failed: game not found") }
            logger.info("Game $game: Get game state successful")
            return@run Either.Right(game.state)
        }
    }

    fun getGame(gameId: Int): GameResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameError.GameNotFound)
                .also { logger.info("Game $gameId: Get game failed: game not found") }
            logger.info("Game $gameId: Get game successful")
            Either.Right(game)
        }
    }

    fun getCurrentGameByUser(userId: Int): GameByUserResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGameByUser(userId) ?: return@run Either.Left(GameByUserError.GameNotFound)
            val player = game.getUser(userId)
            Either.Right(game to player)
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
                .also { logger.info("Game $gameId: Delete game failed: game not found") }
            db.removeGame(gameId)
            logger.info("Game $gameId: Delete game successful")
            return@run Either.Right(Unit)
        }
    }
}
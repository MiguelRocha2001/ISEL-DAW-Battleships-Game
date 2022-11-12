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
            Either.Right(game.gameId)
        }
    }

    fun placeShips(gameId: Int, userId: Int, ships: List<Triple<ShipType, Coordinate, Orientation>>): PlaceShipsResult {
        return transactionManager.run {
            val db = it.gamesRepository
            var game = db.getGame(gameId) ?: return@run Either.Left(PlaceShipsError.GameNotFound)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            if (game.state != GameState.FLEET_SETUP)
                return@run Either.Left(PlaceShipsError.ActionNotPermitted)
            val shipIds = ships.map { generateRandomId() } // generated ids for the ships
            ships.forEachIndexed { idx, (shipType, coordinate, orientation) ->
                game = game.placeShip(shipIds[idx], shipType, coordinate, orientation, player)
                    ?: return@run Either.Left(PlaceShipsError.InvalidMove)
            }
            replaceGame(db, game)
            return@run Either.Right(shipIds)
        }
    }

    fun rotateShip(gameId: Int, userId: Int, shipId: Int, newOrientation: Orientation): RotateShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(RotateShipError.GameNotFound)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(RotateShipError.ActionNotPermitted)
            val newGame = game.rotateShip(shipId, newOrientation, player)
                ?: return@run Either.Left(RotateShipError.InvalidMove)
            replaceGame(db, newGame)
            return@run Either.Right(Unit)
        }
    }

    fun moveShip(gameId: Int, userId: Int, shipId: Int, position: Coordinate, newCoordinate: Coordinate): MoveShipResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(MoveShipError.GameNotFound)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(MoveShipError.ActionNotPermitted)
            val newGame = game.moveShip(shipId, position, newCoordinate, player)
                ?: return@run Either.Left(MoveShipError.InvalidMove)
            replaceGame(db, newGame)
            return@run Either.Right(Unit)
        }
    }

    fun updateFleetState(gameId: Int, userId: Int, confirmed: Boolean): FleetConfirmationResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?:
                return@run Either.Left(FleetConfirmationError.GameNotFound)
            if(game.state != GameState.FLEET_SETUP)
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            if(game.getBoard(player).isConfirmed())
                return@run Either.Left(FleetConfirmationError.ActionNotPermitted)
            if (!confirmed) return@run Either.Right(Unit)
            val newGame = game.updateFleetState(player)
            replaceGame(db, newGame)
            Either.Right(Unit)
        }
    }

    fun placeShot(gameId: Int, userId: Int, c: Coordinate): PlaceShotResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?:
                return@run Either.Left(PlaceShotError.ActionNotPermitted)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            if(game.state != GameState.BATTLE)
                return@run Either.Left(PlaceShotError.ActionNotPermitted)
            val newGame = game.placeShot(userId, c, player)
                ?: return@run Either.Left(PlaceShotError.InvalidMove)
            replaceGame(db, newGame)
            return@run Either.Right(Unit)
        }
    }

    fun getMyFleetLayout(gameId: Int, userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            val player = if(userId == game.player1) Player.ONE else Player.TWO
            return@run Either.Right(game.getBoard(player))
        }
    }

    fun getOpponentFleet(gameId: Int, userId: Int): BoardResult {
        return transactionManager.run {
            val db = it.gamesRepository
            val game = db.getGame(gameId) ?: return@run Either.Left(GameSearchError.GameNotFound)
            val opponent = if(userId == game.player1) Player.TWO else Player.ONE
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

    private fun replaceGame(db: GamesRepository, game: Game) {
        db.removeGame(game.gameId)
        db.saveGame(game)
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
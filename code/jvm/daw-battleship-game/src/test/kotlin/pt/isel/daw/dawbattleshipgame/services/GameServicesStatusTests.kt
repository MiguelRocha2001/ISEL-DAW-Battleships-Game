package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.*
import pt.isel.daw.dawbattleshipgame.utils.*

class GameServicesStatusTests {
    private val configuration = getGameTestConfiguration()

    @Test
    fun create_and_join_game() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            createGame(transactionManager, userPair.first, userPair.second, configuration)
        }
    }

    @Test
    fun getMyAndOpponentFleetLayout() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.first, ShipType.CRUISER, Coordinate(5, 5), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL)

            val boardGameSecond = gameServices.getOpponentFleet(gameId, userPair.first) as Either.Right

            gameServices.updateFleetState(gameId, userPair.first)
            val boardGameFirst = gameServices.getMyFleetLayout(gameId, userPair.first) as Either.Right
            //check if the ships are in the right place
            assertTrue(boardGameFirst.value.isShip(Coordinate(2, 3)))
            assertTrue(boardGameFirst.value.isShip(Coordinate(5, 5)))
            assertTrue(boardGameSecond.value.isShip(Coordinate(1, 1)))
            assertTrue(boardGameSecond.value.isShip(Coordinate(5, 5)))

            //check the confirmation of the fleet
            assertTrue(boardGameFirst.value.isConfirmed())
            assertFalse(boardGameSecond.value.isConfirmed())

            //check if all the ships are included
            assertTrue(boardGameFirst.value.getShips().size == 2)
            assertTrue(boardGameSecond.value.getShips().size == 2)

        }
    }

    @Test
    fun getGameState(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

            var gameStateFirst = gameServices.getGameState(gameId) as Either.Right
            var gameStateSecond = gameServices.getGameState(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, gameStateFirst.value)
            assertEquals(GameState.FLEET_SETUP, gameStateSecond.value)

            gameServices.updateFleetState(gameId, userPair.first)
            gameServices.updateFleetState(gameId, userPair.second)

            gameStateFirst = gameServices.getGameState(gameId) as Either.Right
            gameStateSecond = gameServices.getGameState(gameId) as Either.Right
            assertEquals(GameState.BATTLE, gameStateFirst.value)
            assertEquals(GameState.BATTLE, gameStateSecond.value)
        }
    }

    @Test
    fun getGameStateOfNonExistingGame(){
        testWithTransactionManagerAndRollback { transactionManager ->
            createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            val nonExistentGameId = 999999999

            val gameStateFirst = gameServices.getGameState(nonExistentGameId) as Either.Left
            val gameStateSecond = gameServices.getGameState(nonExistentGameId) as Either.Left
            assertEquals(GameStateError.GameNotFound, gameStateFirst.value)
            assertEquals(GameStateError.GameNotFound, gameStateSecond.value)
        }
    }

    @Test
    fun getGame(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

            var game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)
            assertTrue(game.value.board1.isShip(Coordinate(2,3)))
            assertEquals(ShipType.BATTLESHIP, game.value.board1.getShips().first().type)
            assertEquals(userPair.first, game.value.player1)


            assertTrue(game.value.board2.isShip(Coordinate(1,1)))
            assertEquals(ShipType.CARRIER, game.value.board2.getShips().first().type)
            assertEquals(userPair.second, game.value.player2)

            gameServices.updateFleetState(gameId, userPair.first)
            gameServices.updateFleetState(gameId, userPair.second)

            game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.BATTLE, game.value.state)
        }
    }

    @Test
    fun getInvalidGame(){
        testWithTransactionManagerAndRollback { transactionManager->
            val gameServices = GameServices(transactionManager)

            val game = gameServices.getGame(-1) as Either.Left
            assertEquals(GameError.GameNotFound, game.value)
        }
    }

    @Test
    fun deleteGame(){
      testWithTransactionManagerAndRollback {
        transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

            gameServices.deleteGame(gameId) as Either.Right

            val game = gameServices.getGame(gameId) as Either.Left
            assertEquals(GameError.GameNotFound, game.value)

      }
    }

    @Test
    fun deleteNonExistingGame(){
        testWithTransactionManagerAndRollback {
                transactionManager ->

            val gameServices = GameServices(transactionManager)
            val invalidDeletion = gameServices.deleteGame(-1) as Either.Left
            assertEquals(DeleteGameError.GameNotFound, invalidDeletion.value)
        }
    }
}
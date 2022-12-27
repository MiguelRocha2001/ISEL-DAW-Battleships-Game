package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.DeleteGameError
import pt.isel.daw.dawbattleshipgame.services.game.GameError
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.game.GameStateError
import pt.isel.daw.dawbattleshipgame.utils.createGame
import pt.isel.daw.dawbattleshipgame.utils.createUserPair
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration2
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameServicesStatusTests {
    private val conf = getGameTestConfiguration2()

    @Test
    fun create_and_join_game() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            createGame(gameServices, userPair.first, userPair.second, conf)
        }
    }

    @Test
    fun getMyAndOpponentFleetLayout() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            println(conf)
            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, conf)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.BATTLESHIP, Coordinate(5, 1), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(5, 9), Orientation.VERTICAL)

            val boardGameSecond = gameServices.getOpponentFleet(userPair.first) as Either.Right

            gameServices.updateFleetState(userPair.first, true)
            val boardGameFirst = gameServices.getMyFleetLayout(userPair.first) as Either.Right
            //check if the ships are in the right place
            assertTrue(boardGameFirst.value.isShip(Coordinate(1, 3)))
            assertTrue(boardGameFirst.value.isShip(Coordinate(7, 7)))
            assertTrue(boardGameSecond.value.isShip(Coordinate(5, 1)))
            assertTrue(boardGameSecond.value.isShip(Coordinate(5, 9)))

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
            val gameId = createGame(gameServices, userPair.first, userPair.second, conf)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.BATTLESHIP, Coordinate(5, 1), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(5, 9), Orientation.VERTICAL)

            var gameStateFirst = gameServices.getGameState(gameId) as Either.Right
            var gameStateSecond = gameServices.getGameState(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, gameStateFirst.value)
            assertEquals(GameState.FLEET_SETUP, gameStateSecond.value)

            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)

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
    fun getGame() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, conf)

            // places some ships...
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.BATTLESHIP, Coordinate(5, 1), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(5, 9), Orientation.VERTICAL)

            var game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)
            assertTrue(game.value.board1.isShip(Coordinate(2,3)))
            assertEquals(ShipType.BATTLESHIP, game.value.board1.getShips().first().type)
            assertEquals(userPair.first, game.value.player1)


            assertTrue(game.value.board2.isShip(Coordinate(6,9)))
            assertEquals(ShipType.BATTLESHIP, game.value.board2.getShips().first().type)
            assertEquals(userPair.second, game.value.player2)

            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)

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
            val gameId = createGame(gameServices, userPair.first, userPair.second, conf)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

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
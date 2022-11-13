package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.*
import pt.isel.daw.dawbattleshipgame.utils.*

class GamePlaceShotServicesTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun placeShot() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)


            //var game = gameServices.getGame(gameId) as Either.Right
            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)
            var game = gameServices.getGame(gameId)
            assertTrue(game is Either.Right)

            //place all the shots with the objective of sinking all player two ships
            gameServices.placeShot(userPair.first, Coordinate(1,1))
            gameServices.placeShot(userPair.second, Coordinate(2,2))
            gameServices.placeShot(userPair.first, Coordinate(2,1))
            gameServices.placeShot(userPair.second, Coordinate(4,1))
            gameServices.placeShot(userPair.first, Coordinate(3,1))
            gameServices.placeShot(userPair.second, Coordinate(5,3))
            gameServices.placeShot(userPair.first, Coordinate(4,1))
            gameServices.placeShot(userPair.second, Coordinate(1,4))

            //game before last shot
            gameServices.getGame(gameId) as? Either.Right ?: fail("Expected game result")

            val gameResult = gameServices.placeShot(userPair.first, Coordinate(5,1)) as? Either.Right
                ?: fail("Expected game result")
            assertEquals(GameState.FINISHED, gameResult.value)

            //game after last shot
            game = gameServices.getGame(gameId) as? Either.Right ?: fail("Expected game result")
            assertEquals(GameState.FINISHED,game.value.state)
            assertEquals(userPair.first ,game.value.winner)
            assertTrue(game.value.board2["A5".toCoordinate()].isHit)
            println(game.value.board2.toString())
        }
    }

    @Test
    fun invalidPlaceShot() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)
            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)

            gameServices.placeShot(userPair.first, Coordinate(1,1)) // valid
            gameServices.placeShot(userPair.second, Coordinate(2,2)) // valid

            val result = gameServices.placeShot(userPair.first, Coordinate(1,1)) // same coordinate
            assertEquals(Either.Left(PlaceShotError.InvalidMove), result)

            val result2 = gameServices.placeShot(userPair.second, Coordinate(3,3)) // not its turn
            assertEquals(Either.Left(PlaceShotError.InvalidMove), result2) // TODO should be ActionNotPermitted
        }
    }
}
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
    private val configuration = getGameTestConfiguration3()

    @Test
    fun placeShot() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameConfig = getGameTestConfiguration6()
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, gameConfig)

            // apply some actions with player_1
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(1, 1), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(3, 1), Orientation.HORIZONTAL)



            //var game = gameServices.getGame(gameId) as Either.Right
            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)
            var game = gameServices.getGame(gameId)
            assertTrue(game is Either.Right)

            //place all the shots with the objective of sinking all player two ships
            gameServices.placeShots(userPair.first, listOf(Coordinate(1,1)))
            gameServices.placeShots(userPair.second, listOf(Coordinate(3,2)))
            gameServices.placeShots(userPair.first, listOf(Coordinate(1,2)))


            //game before last shot
            game = gameServices.getGame(gameId) as? Either.Right ?: fail("Expected game result")
            game.value.debug()
            assertEquals(GameState.FINISHED, game.value.state)

            //game after last shot
            assertEquals(userPair.first ,game.value.winner)
            assertTrue(game.value.board2["A1".toCoordinate()].isHit)
        }
    }

    @Test
    fun invalidPlaceShot() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(2, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(1, 1), Orientation.VERTICAL)
            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)

            // valid
            val placeShotResult1 = gameServices.placeShots(
                userPair.first,
                listOf(
                    Coordinate(1, 1),
                    Coordinate(1, 2),
                    Coordinate(1, 3),
                    Coordinate(1, 4),
                    Coordinate(1, 5),
                )
            )
            assertEquals(Either.Right(Unit), placeShotResult1)

            // valid
            val placeShotResult2 = gameServices.placeShots(
                userPair.second,
                listOf(
                    Coordinate(2, 2),
                    Coordinate(2, 3),
                    Coordinate(2, 4),
                    Coordinate(2, 5),
                    Coordinate(2, 6),
                )
            )
            assertEquals(Either.Right(Unit), placeShotResult2)

            // same coordinate
            val result = gameServices.placeShots(
                userPair.first,
                listOf(
                    Coordinate(1, 1),
                    Coordinate(2, 2),
                    Coordinate(2, 3),
                    Coordinate(2, 4),
                    Coordinate(2, 5),
                )
            )
            assertEquals(Either.Left(PlaceShotError.InvalidShot), result)

            // not its turn
            val result2 = gameServices.placeShots(
                userPair.second,
                listOf(
                    Coordinate(3, 3),
                    Coordinate(3, 4),
                    Coordinate(3, 5),
                    Coordinate(3, 6),
                    Coordinate(3, 7),
                )
            )
            assertEquals(Either.Left(PlaceShotError.ActionNotPermitted), result2) // TODO should be ActionNotPermitted
        }
    }
}
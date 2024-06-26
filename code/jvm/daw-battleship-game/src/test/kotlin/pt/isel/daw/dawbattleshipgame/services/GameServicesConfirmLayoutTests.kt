package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.utils.createGame
import pt.isel.daw.dawbattleshipgame.utils.createUserPair
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameServicesConfirmLayoutTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun confirmFleet() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            val res1 = gameServices.placeShips(
                userPair.first,
                listOf(
                    Triple(ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL),
                    Triple(ShipType.DESTROYER, Coordinate(6, 8), Orientation.VERTICAL),
                    Triple(ShipType.CARRIER, Coordinate(1, 8), Orientation.HORIZONTAL),
                    Triple(ShipType.CRUISER, Coordinate(11, 11), Orientation.HORIZONTAL),
                    Triple(ShipType.SUBMARINE, Coordinate(13, 5), Orientation.HORIZONTAL),
                )
            )
            assertTrue(res1 is Either.Right)

            val res2 = gameServices.placeShips(
                userPair.second,
                listOf(
                    Triple(ShipType.BATTLESHIP, Coordinate(1, 5), Orientation.HORIZONTAL),
                    Triple(ShipType.DESTROYER, Coordinate(1, 12), Orientation.VERTICAL),
                    Triple(ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL),
                    Triple(ShipType.CRUISER, Coordinate(12, 6), Orientation.HORIZONTAL),
                    Triple(ShipType.SUBMARINE, Coordinate(10, 8), Orientation.HORIZONTAL),
                )
            )
            assertTrue(res2 is Either.Right)

            val game1 = gameServices.getGame(gameId) ?: fail { "Game not found" }
            assertEquals(GameState.FLEET_SETUP, game1.state)

            val confRes1 = gameServices.updateFleetState(userPair.first, true)
            assertTrue(confRes1 is Either.Right)

            val game2 = gameServices.getGame(gameId) ?: fail { "Game not found" }
            assertEquals(GameState.FLEET_SETUP, game2.state)

            val confRes2 = gameServices.updateFleetState(userPair.second, true)
            assertTrue(confRes2 is Either.Right)

            val game3 = gameServices.getGame(gameId) ?: fail { "Game not found" }
            assertEquals(GameState.BATTLE, game3.state)
        }
    }
}
package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.utils.createGame
import pt.isel.daw.dawbattleshipgame.utils.createUserPair
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameServicesRotateShipTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun rotateAndMoveShip() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(gameServices, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.getGame(gameId) ?: fail { "Game not found" }

            gameServices.updateShip(userPair.first, Coordinate(2, 3))
            val foundGame = gameServices.getGame(gameId) ?: fail { "Game not found" }

            assertTrue(foundGame.board1.isShip(Coordinate(2, 3)))
            assertTrue(foundGame.board1.isShip(Coordinate(2, 4)))
            assertTrue(foundGame.board1.isShip(Coordinate(2, 5)))
            assertTrue(foundGame.board1.isShip(Coordinate(2, 6)))
            gameServices.updateShip(userPair.first, Coordinate(2, 3), Coordinate(3, 3))
            assertFalse(foundGame.board1.isShip(Coordinate(3, 3)))
            assertFalse(foundGame.board1.isShip(Coordinate(3, 4)))
            assertFalse(foundGame.board1.isShip(Coordinate(3, 5)))
            assertFalse(foundGame.board1.isShip(Coordinate(3, 6)))
        }
    }
}
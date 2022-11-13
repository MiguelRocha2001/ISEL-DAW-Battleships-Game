package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.*
import pt.isel.daw.dawbattleshipgame.utils.*

class GameServicesRotateShipTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun rotateAndMoveShip() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            var game = gameServices.getGame(gameId) as Either.Right

            println(game.value.board1.toString())
            gameServices.rotateShip(userPair.first, Coordinate(2, 3))
            game = gameServices.getGame(gameId) as Either.Right
            println(game.value.board1.toString())

            when (val foundGame = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $foundGame")
                is Either.Right -> {
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 4)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 5)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 6)))
                    gameServices.moveShip(userPair.first, Coordinate(2, 3), Coordinate(3, 3))
                    assertFalse(foundGame.value.board1.isShip(Coordinate(3, 3)))
                    assertFalse(foundGame.value.board1.isShip(Coordinate(3, 4)))
                    assertFalse(foundGame.value.board1.isShip(Coordinate(3, 5)))
                    assertFalse(foundGame.value.board1.isShip(Coordinate(3, 6)))
                }
                //  assertTrue(foundGame.value.board1.isShip(Coordinate(2,3)))
            }
        }
    }
}
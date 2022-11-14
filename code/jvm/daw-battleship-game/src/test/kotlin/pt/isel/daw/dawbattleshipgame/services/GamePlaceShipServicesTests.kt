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

class GamePlaceShipServicesTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun placeShip() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameServices = GameServices(transactionManager)

            val userPair = createUserPair(transactionManager)

            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)
            val placeShip1Result = placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL) as Either.Right
            val placeShip2Result = placeShip(gameServices, userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL) as Either.Right

            when (val game = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $game")
                is Either.Right -> {
                    assertEquals(1, game.value.board1.getShips().size)
                    assertEquals(1, game.value.board2.getShips().size)
                }
            }
        }
    }

    @Test
    fun placeShipWithoutCreatedGame(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)

            val palaceShipInvalidPosition = placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipsError.GameNotFound, palaceShipInvalidPosition.value)

            val placeShipOutOfBoard = placeShip(gameServices, userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipsError.GameNotFound, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeTwoShipsOfSameType() { //IT IS INVALID TO HAVE TWO SHIPS OF THE SAME TYPE
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            val gameId = createGame(it, userPair.first, userPair.second, configuration)

            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL) as Either.Right
            val sameTypeShip = placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 1), Orientation.HORIZONTAL) as Either.Left

            assertEquals(PlaceShipsError.InvalidMove, sameTypeShip.value)
        }
    }

    @Test
    fun placeTwoShipsInSameSpot(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)

            createGame(it, userPair.first, userPair.second, configuration)

            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1 ,1), Orientation.HORIZONTAL) as Either.Right

            val overlayShip = placeShip(gameServices, userPair.first, ShipType.SUBMARINE, Coordinate(1, 3), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipsError.InvalidMove, overlayShip.value)
        }
    }

    @Test
    fun invalidPositionsPlaceShip(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)

            createGame(it, userPair.first, userPair.second, configuration)

            val palaceShipInvalidPosition = placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(15, 15), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipsError.InvalidMove, palaceShipInvalidPosition.value)

            val placeShipOutOfBoard = placeShip(gameServices, userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipsError.InvalidMove, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeShipInWrongPhase(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            createGame(it, userPair.first, userPair.second, getGameTestConfiguration2())

            // valid place ships
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.BATTLESHIP, Coordinate(5, 1), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.DESTROYER, Coordinate(5, 9), Orientation.VERTICAL)


            gameServices.updateFleetState(userPair.first, true)
            gameServices.updateFleetState(userPair.second, true)

            // invalid place ships
            val invalidPlace1 = placeShip(gameServices, userPair.first, ShipType.CARRIER, Coordinate(1, 5), Orientation.HORIZONTAL) as Either.Left
            val invalidPlace2 = placeShip(gameServices, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL) as Either.Left

            assertEquals(PlaceShipsError.ActionNotPermitted, invalidPlace1.value)
            assertEquals(PlaceShipsError.ActionNotPermitted, invalidPlace2.value)

        }
    }
}
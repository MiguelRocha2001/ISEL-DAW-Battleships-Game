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

class GamePlaceShipServicesTests {
    private val configuration = getGameTestConfiguration()

    @Test
    fun placeShip() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameServices = GameServices(transactionManager)

            val userPair = createUserPair(transactionManager)

            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)
            val placeShip1Result =
                gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            when (placeShip1Result) {
                is Either.Left -> fail("Unexpected $placeShip1Result")
                is Either.Right -> assertEquals(GameState.FLEET_SETUP,placeShip1Result.value)
            }

            val placeShip2Result =
                gameServices.placeShip(gameId, userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL)
            when (placeShip2Result) {
                is Either.Left -> fail("Unexpected $placeShip2Result")
                is Either.Right -> assertEquals(GameState.FLEET_SETUP, placeShip2Result.value)
            }

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
            val nonExistingGameId = 12345

            val palaceShipInvalidPosition = gameServices.placeShip(nonExistingGameId, userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipError.GameNotFound, palaceShipInvalidPosition.value)

            val placeShipOutOfBoard = gameServices.placeShip(nonExistingGameId, userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.GameNotFound, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeTwoShipsOfSameType() { //IT IS INVALID TO HAVE TWO SHIPS OF THE SAME TYPE
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            val gameId = createGame(it, userPair.first, userPair.second, configuration)

            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL) as Either.Right
            val sameTypeShip = gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(1, 1), Orientation.HORIZONTAL) as Either.Left

            assertEquals(PlaceShipError.InvalidMove, sameTypeShip.value)
        }
    }

    @Test
    fun placeTwoShipsInSameSpot(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            val gameId = createGame(it, userPair.first, userPair.second, configuration)

            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(1 ,1), Orientation.HORIZONTAL) as Either.Right

            val overlayShip = gameServices.placeShip(gameId, userPair.first, ShipType.SUBMARINE, Coordinate(1, 3), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, overlayShip.value)
        }
    }

    @Test
    fun invalidPositionsPlaceShip(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)

            val gameId = createGame(it, userPair.first, userPair.second, configuration)

            val palaceShipInvalidPosition = gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, palaceShipInvalidPosition.value)

            val placeShipOutOfBoard = gameServices.placeShip(gameId, userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeShipInWrongPhase(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            val gameId = createGame(it, userPair.first, userPair.second, configuration)

            // valid place ships
            gameServices.placeShip(gameId, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.HORIZONTAL)
            gameServices.placeShip(gameId, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            gameServices.placeShip(gameId, userPair.second, ShipType.CRUISER, Coordinate(5, 9), Orientation.VERTICAL)


            gameServices.confirmFleet(gameId, userPair.first)
            gameServices.confirmFleet(gameId, userPair.second)

            // invalid place ships
            val invalidPlace1 = gameServices.placeShip(gameId, userPair.first, ShipType.CARRIER, Coordinate(1, 5), Orientation.HORIZONTAL) as Either.Left
            val invalidPlace2 = gameServices.placeShip(gameId, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL) as Either.Left

            assertEquals(PlaceShipError.ActionNotPermitted, invalidPlace1.value)
            assertEquals(PlaceShipError.ActionNotPermitted, invalidPlace2.value)

        }
    }
}
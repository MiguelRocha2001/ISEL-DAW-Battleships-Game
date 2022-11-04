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

class GameServicesTests {
    private val configuration = getGameTestConfiguration()

    @Test
    fun create_and_join_game() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            createGame(transactionManager, userPair.first, userPair.second, configuration)
        }
    }

    @Test
    fun placeShip() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameServices = GameServices(transactionManager)

            val userPair = createUserPair(transactionManager)

            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)
            val placeShip1Result =
                gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            when (placeShip1Result) {
                is Either.Left -> fail("Unexpected $placeShip1Result")
                is Either.Right -> assertEquals(GameState.FLEET_SETUP,placeShip1Result.value)
            }

            val placeShip2Result =
                gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL)
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
            val palaceShipInvalidPosition = gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipError.GameNotFound, palaceShipInvalidPosition.value)
            val placeShipOutOfBoard = gameServices.placeShip(userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.GameNotFound, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeTwoShipsOfSameType(){//IT IS INVALID TO HAVE TWO SHIPS OF THE SAME TYPE
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            createGame(it, userPair.first, userPair.second, configuration)
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL) as Either.Right
            val sameTypeShip = gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(1, 1), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, sameTypeShip.value)
        }
    }

    @Test
    fun placeTwoShipsInSameSpot(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            createGame(it, userPair.first, userPair.second, configuration)
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(1 ,1), Orientation.HORIZONTAL) as Either.Right

            val overlayShip = gameServices.placeShip(userPair.first, ShipType.SUBMARINE, Coordinate(1, 3), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, overlayShip.value)
        }
    }

    @Test
    fun invalidPositionsPlaceShip(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)

            createGame(it, userPair.first, userPair.second, configuration)

            val palaceShipInvalidPosition = gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.HORIZONTAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, palaceShipInvalidPosition.value)

            val placeShipOutOfBoard = gameServices.placeShip(userPair.first, ShipType.SUBMARINE, Coordinate(90, 90), Orientation.VERTICAL) as Either.Left
            assertEquals(PlaceShipError.InvalidMove, placeShipOutOfBoard.value)
        }
    }

    @Test
    fun placeShipInWrongPhase(){
        testWithTransactionManagerAndRollback {
            val gameServices = GameServices(it)
            val userPair = createUserPair(it)
            createGame(it, userPair.first, userPair.second, configuration)

            //valid place ships
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.HORIZONTAL)
            gameServices.placeShip(userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CRUISER, Coordinate(5, 9), Orientation.VERTICAL)


            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)

            //invalid place ships

            val invalidPlace1 = gameServices.placeShip(userPair.first, ShipType.CARRIER, Coordinate(1, 5), Orientation.HORIZONTAL) as Either.Left
            val invalidPlace2 = gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL) as Either.Left

            assertEquals(PlaceShipError.ActionNotPermitted, invalidPlace1.value)
            assertEquals(PlaceShipError.ActionNotPermitted, invalidPlace2.value)

        }
    }

    @Test
    fun rotateAndMoveShip() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
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

    @Test
    fun invalidRotationOfShip(){
        testWithTransactionManagerAndRollback {  transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            //valid place ship
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(9, 9), Orientation.VERTICAL)
            gameServices.placeShip(userPair.first, ShipType.SUBMARINE, Coordinate(1, 1), Orientation.VERTICAL)

            //invalid rotation operations
            val invalidRotation = gameServices.rotateShip(userPair.first, Coordinate(9, 9))
            val rotationOfNonShipPanel = gameServices.rotateShip(userPair.first, Coordinate(2, 2))

            assertEquals(invalidRotation, Either.Left(RotateShipError.InvalidMove))
            assertEquals(rotationOfNonShipPanel, Either.Left(RotateShipError.InvalidMove))

            //valid rotations in wrong phase

            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)

            val invalidRotation2 = gameServices.rotateShip(userPair.first, Coordinate(1, 1)) as Either.Left
            assertEquals(RotateShipError.ActionNotPermitted, invalidRotation2.value)
        }
    }

    @Test
    fun confirmFleet(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.HORIZONTAL)
            gameServices.placeShip(userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CRUISER, Coordinate(5, 9), Orientation.VERTICAL)
            gameServices.placeShip(userPair.first, ShipType.CARRIER, Coordinate(1, 5), Orientation.HORIZONTAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)


            var game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)

            gameServices.confirmFleet(userPair.first)
            game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)

            gameServices.confirmFleet(userPair.second)
            game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.BATTLE, game.value.state)
        }
    }

    @Test
    fun placeShot(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)


            //var game = gameServices.getGame(gameId) as Either.Right
            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)
            var game = gameServices.getGame(gameId) as Either.Right

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
            createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)
            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)

            gameServices.placeShot(userPair.first, Coordinate(1,1)) // valid
            gameServices.placeShot(userPair.second, Coordinate(2,2)) // valid

            val result = gameServices.placeShot(userPair.first, Coordinate(1,1)) // same coordinate
            assertEquals(Either.Left(PlaceShotError.InvalidMove), result)

            val result2 = gameServices.placeShot(userPair.second, Coordinate(3,3)) // not its turn
            assertEquals(Either.Left(PlaceShotError.InvalidMove), result2) // TODO should be ActionNotPermitted


        }
    }

    @Test
    fun getMyAndOpponentFleetLayout() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.first, ShipType.CRUISER, Coordinate(5, 5), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL)

            val boardGameSecond = gameServices.getOpponentFleet(userPair.first) as Either.Right

            gameServices.confirmFleet(userPair.first)
            val boardGameFirst = gameServices.getMyFleetLayout(userPair.first) as Either.Right
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
            createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

            var gameStateFirst = gameServices.getGameState(userPair.first) as Either.Right
            var gameStateSecond = gameServices.getGameState(userPair.first) as Either.Right
            assertEquals(GameState.FLEET_SETUP, gameStateFirst.value)
            assertEquals(GameState.FLEET_SETUP, gameStateSecond.value)

            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)

            gameStateFirst = gameServices.getGameState(userPair.first) as Either.Right
            gameStateSecond = gameServices.getGameState(userPair.first) as Either.Right
            assertEquals(GameState.BATTLE, gameStateFirst.value)
            assertEquals(GameState.BATTLE, gameStateSecond.value)
        }
    }

    @Test
    fun getGameStateOfNonExistingGame(){
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            val gameStateFirst = gameServices.getGameState(userPair.first) as Either.Left
            val gameStateSecond = gameServices.getGameState(userPair.first) as Either.Left
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
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

            var game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)
            assertTrue(game.value.board1.isShip(Coordinate(2,3)))
            assertEquals(ShipType.BATTLESHIP, game.value.board1.getShips().first().type)
            assertEquals(userPair.first, game.value.player1)


            assertTrue(game.value.board2.isShip(Coordinate(1,1)))
            assertEquals(ShipType.CARRIER, game.value.board2.getShips().first().type)
            assertEquals(userPair.second, game.value.player2)

            gameServices.confirmFleet(userPair.first)
            gameServices.confirmFleet(userPair.second)

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
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.placeShip(userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)

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
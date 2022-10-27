package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.utils.Sha256TokenEncoder
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManager
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback
import javax.validation.constraints.AssertTrue

class GameServicesTests {
    val configuration = getGameTestConfiguration()

    fun createUserPair(transactionManager: TransactionManager): Pair<Int, Int> {

        val userService = UserServices(
            transactionManager,
            UserLogic(),
            BCryptPasswordEncoder(),
            Sha256TokenEncoder(),
        )
        // Create User 1
        val player1 = "user1"
        var createUserResult = userService.createUser(player1, "Password1")
        // then: the creation is successful
        when (createUserResult) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
        }
        val player1Test = createUserResult.value.toInt()

        // Create User
        val player2 = "user2"
        createUserResult = userService.createUser(player2, "Password2")
        // then: the creation is successful
        when (createUserResult) {
            is Either.Left -> fail("Unexpected $createUserResult")
            is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
        }
        val player2Test = createUserResult.value.toInt()
        return Pair(player1Test, player2Test)
    }

    @Test
    fun create_and_join_game() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            // Create Game
            val addPlayerQueueResponse =
                gameServices.startGame(userPair.first, getGameTestConfiguration()) // player 1 will be put in a queue
            when (addPlayerQueueResponse) {
                is Either.Left -> fail("Unexpected $addPlayerQueueResponse")
                is Either.Right -> assertTrue(addPlayerQueueResponse.value.name == "NOT_STARTED")
            }

            val createGameResult = gameServices.startGame(userPair.second, getGameTestConfiguration())

            when (createGameResult) {
                is Either.Left -> fail("Unexpected $createGameResult")
                is Either.Right -> assertTrue(createGameResult.value.name == "FLEET_SETUP")
            }

        }
    }

    @Test
    fun getGameUsingGameIdAndUserId() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            // Create Game
            gameServices.startGame(userPair.first, getGameTestConfiguration()) // player 1 will be put in a queue
            gameServices.startGame(userPair.second, getGameTestConfiguration())// player 2 will start the game
            val gameId:Int
            when (val user1Game = gameServices.getGameIdByUser(userPair.first)) {
                is Either.Left -> fail("Unexpected $user1Game")
                is Either.Right -> {
                    assertNotNull(user1Game.value)
                    gameId = user1Game.value
                }
            }

            when (val foundGame = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $foundGame")
                is Either.Right -> {
                    assertTrue(foundGame.value.player2 == userPair.second)
                    assertTrue(foundGame.value.player1 == userPair.first)
                    assertTrue(foundGame.value.gameId == gameId)
                }
            }


        }

    }

    @Test
    fun placeShip() {
        testWithTransactionManagerAndRollback { transactionManager -> // TODO FIXTEST

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            // Create Game
            gameServices.startGame(userPair.first, getGameTestConfiguration()) // player 1 will be put in a queue
            gameServices.startGame(userPair.second, getGameTestConfiguration())// player 2 will start the game
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2,3), Orientation.VERTICAL)
            val afterFirstShipMove = gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5,5), Orientation.VERTICAL)

            when (afterFirstShipMove) {
                is Either.Left -> fail("Unexpected $afterFirstShipMove")
                is Either.Right -> {
                    assertTrue(afterFirstShipMove.value.name == "FLEET_SETUP")
                }
            }

            val gameId:Int
            when (val user2Game = gameServices.getGameIdByUser(userPair.second)) {
                is Either.Left -> fail("Unexpected $user2Game")
                is Either.Right -> {
                    assertNotNull(user2Game.value)
                    gameId = user2Game.value
                }
            }

            when (val foundGame = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $foundGame")
                is Either.Right -> {
                    //assertTrue(foundGame.value.board1.isShip(Coordinate(2,3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(5,5)))
                }
            }
         }
    }

    @Test
    fun rotateAndMoveShip() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)
            // Create Game
            gameServices.startGame(userPair.first, getGameTestConfiguration()) // player 1 will be put in a queue
            gameServices.startGame(userPair.second, getGameTestConfiguration())// player 2 will start the game
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2,3), Orientation.VERTICAL)
            gameServices.rotateShip(userPair.first,Coordinate(2,3))

            val gameId:Int
            when (val user2Game = gameServices.getGameIdByUser(userPair.second)) {
                is Either.Left -> fail("Unexpected $user2Game")
                is Either.Right -> {
                    assertNotNull(user2Game.value)
                    gameId = user2Game.value
                }
            }

            when (val foundGame = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $foundGame")
                is Either.Right -> {
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2,3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2,4)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2,5)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2,6)))
                    gameServices.moveShip(userPair.first,Coordinate(2,3),Coordinate(3,3))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3,3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3,4)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3,5)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3,6)))
                }
                  //  assertTrue(foundGame.value.board1.isShip(Coordinate(2,3)))
                }
            }
        }

    fun confirmFleet(){//TODO
    }

    fun placeShot(){//TODO
    }


    fun getMyFleetLayout(){//TODO
    }

    fun getOpponentFleet(){//TODO
    }

    fun getGameState(){//TODO
    }

    fun getGame(){//TODO
    }


}
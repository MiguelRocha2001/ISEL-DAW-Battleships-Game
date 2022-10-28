package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.GameState
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.utils.Sha256TokenEncoder
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameServicesTests {
    val configuration = getGameTestConfiguration()

    private fun createUserPair(transactionManager: TransactionManager): Pair<Int, Int> {

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

    /**
     * Test the creation of a game
     * Takes two player and starts a game with them.
     * Then checks if the game was created, if the two players were added to the game and if the game state is the expected one.
     * Also, checks if gameId is equal or greater than 0.
     * @return gameId
     */
    private fun createGame(transactionManager: TransactionManager, player1: Int, player2: Int, configuration: Configuration): Int {
        val gameService = GameServices(transactionManager)
        val gameCreationResult1 = gameService.startGame(player1, configuration)
        when (gameCreationResult1) {
            is Either.Left -> fail("Unexpected $gameCreationResult1")
            is Either.Right -> assertEquals(gameCreationResult1.value, GameState.NOT_STARTED)
        }
        val gameCreationResult2 = gameService.startGame(player2, configuration)
        when (gameCreationResult2) {
            is Either.Left -> fail("Unexpected $gameCreationResult2")
            is Either.Right -> assertEquals(gameCreationResult2.value, GameState.FLEET_SETUP)
        }
        val user1GameIdResult = gameService.getGameIdByUser(player1)
        when (user1GameIdResult) {
            is Either.Left -> fail("Unexpected $gameCreationResult1")
            is Either.Right -> assertTrue(user1GameIdResult.value >= 0)
        }
        val user2GameIdResult = gameService.getGameIdByUser(player2)
        when (user2GameIdResult) {
            is Either.Left -> fail("Unexpected $gameCreationResult2")
            is Either.Right -> assertTrue(user2GameIdResult.value >= 0)
        }
        assertEquals(user1GameIdResult.value, user2GameIdResult.value)

        when (val user1Game = gameService.getGame(user1GameIdResult.value)) {
            is Either.Left -> fail("Unexpected $user1Game")
            is Either.Right -> {
                assertEquals(user1Game.value.gameId, user1GameIdResult.value)
                assertEquals(user1Game.value.player1, player1)
                assertEquals(user1Game.value.player2, player2)
                assertEquals(user1Game.value.state, GameState.FLEET_SETUP)
            }
        }
        val user2Game = gameService.getGame(user2GameIdResult.value)
        when (user2Game) {
            is Either.Left -> fail("Unexpected $user2Game")
            is Either.Right -> {
                assertEquals(user2Game.value.gameId, user2GameIdResult.value)
                assertEquals(user2Game.value.player1, player1)
                assertEquals(user2Game.value.player2, player2)
                assertEquals(user2Game.value.state, GameState.FLEET_SETUP)
            }
        }
        return user1GameIdResult.value
    }

    @Test
    fun create_and_join_game() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            createGame(transactionManager, userPair.first, userPair.second, configuration)
        }
    }

    @Test
    fun placeShip() {
        testWithTransactionManagerAndRollback { transactionManager -> // TODO FIXTEST
            val gameServices = GameServices(transactionManager)

            val userPair = createUserPair(transactionManager)

            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)
            val placeShip1Result =
                gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            when (placeShip1Result) {
                is Either.Left -> fail("Unexpected $placeShip1Result")
                is Either.Right -> assertEquals(placeShip1Result.value, GameState.FLEET_SETUP)
            }

            val placeShip2Result =
                gameServices.placeShip(userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.VERTICAL)
            when (placeShip2Result) {
                is Either.Left -> fail("Unexpected $placeShip2Result")
                is Either.Right -> assertEquals(placeShip2Result.value, GameState.FLEET_SETUP)
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
    fun rotateAndMoveShip() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            gameServices.placeShip(userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            gameServices.rotateShip(userPair.first, Coordinate(2, 3))

            when (val foundGame = gameServices.getGame(gameId)) {
                is Either.Left -> fail("Unexpected $foundGame")
                is Either.Right -> {
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 4)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 5)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(2, 6)))
                    gameServices.moveShip(userPair.first, Coordinate(2, 3), Coordinate(3, 3))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3, 3)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3, 4)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3, 5)))
                    assertTrue(foundGame.value.board1.isShip(Coordinate(3, 6)))
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
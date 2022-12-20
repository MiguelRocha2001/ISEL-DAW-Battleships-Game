package pt.isel.daw.dawbattleshipgame.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.game.CreateGameInputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.ShipTypeInputModel
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.user.UserServices

fun getGameTestConfiguration1() = Configuration(
    boardSize = 15,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)

fun getGameTestConfiguration2() = Configuration(
    boardSize = 10,
    fleet = mapOf(
        ShipType.BATTLESHIP to 4,
        ShipType.DESTROYER to 2
    ),
    shots = 5,
    roundTimeout = 10
)

fun getGameTestConfiguration3() = Configuration(
    boardSize = 8,
    fleet = mapOf(
        Pair(ShipType.DESTROYER, 2)
    ),
    shots = 5,
    roundTimeout = 10
)

fun getGameTestConfiguration4() = Configuration(
    boardSize = 10,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)

fun getGameTestConfiguration5() = Configuration(
    boardSize = 13,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)

fun getCreateGameInputModel() = CreateGameInputModel(
    boardSize = 10,
    shots = 5,
    fleet = mapOf(
        ShipTypeInputModel.CARRIER to 5,
        ShipTypeInputModel.BATTLESHIP to 4,
        ShipTypeInputModel.CRUISER to 3,
        ShipTypeInputModel.SUBMARINE to 3,
        ShipTypeInputModel.DESTROYER to 2
    ),
    roundTimeout = 10
)


fun generateGameId(): Int = (Math.random() * 100000).toInt()

fun getRandomPassword(): String = "A" + (Math.random() * 100000).toString()

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
        is Either.Right -> requireNotNull(createUserResult.value)
    }
    val player1Test = createUserResult.value

    // Create User
    val player2 = "user2"
    createUserResult = userService.createUser(player2, "Password2")
    // then: the creation is successful
    when (createUserResult) {
        is Either.Left -> fail("Unexpected $createUserResult")
        is Either.Right -> requireNotNull(createUserResult.value)
    }
    val player2Test = createUserResult.value
    return Pair(player1Test, player2Test)
}

/**
 * Test the creation of a game
 * Takes two player and starts a game with them.
 * Then checks if the game was created, if the two players were added to the game and if the game state is the expected one.
 * Also, checks if gameId is equal or greater than 0.
 * @return gameId
 */
fun createGame(gameServices: GameServices, player1: Int, player2: Int, configuration: Configuration): Int {
    val gameCreationResult1 = gameServices.startGame(player1, configuration)
    when (gameCreationResult1) {
        is Either.Left -> fail("Unexpected $gameCreationResult1")
        is Either.Right -> {
            Assertions.assertEquals(gameCreationResult1.value.first, GameState.NOT_STARTED)
            Assertions.assertNull(gameCreationResult1.value.second)
        }
    }
    val gameCreationResult2 = gameServices.startGame(player2, configuration)
    when (gameCreationResult2) {
        is Either.Left -> fail("Unexpected $gameCreationResult2")
        is Either.Right -> {
            Assertions.assertEquals(gameCreationResult2.value.first, GameState.FLEET_SETUP)
            Assertions.assertNotNull(gameCreationResult2.value.second)
        }

    }
    val user1GameIdResult = gameServices.getGameIdByUser(player1)
    when (user1GameIdResult) {
        is Either.Left -> fail("Unexpected $gameCreationResult1")
        is Either.Right -> Assertions.assertTrue(user1GameIdResult.value >= 0)
    }
    val user2GameIdResult = gameServices.getGameIdByUser(player2)
    when (user2GameIdResult) {
        is Either.Left -> fail("Unexpected $gameCreationResult2")
        is Either.Right -> Assertions.assertTrue(user2GameIdResult.value >= 0)
    }
    Assertions.assertEquals(user1GameIdResult.value, user2GameIdResult.value)

    val user1Game = gameServices.getGame(user1GameIdResult.value)
    when (user1Game) {
        is Either.Left -> fail("Unexpected $user1Game")
        is Either.Right -> {
            Assertions.assertEquals(user1Game.value.id, user1GameIdResult.value)
            Assertions.assertEquals(user1Game.value.player1, player1)
            Assertions.assertEquals(user1Game.value.player2, player2)
            Assertions.assertEquals(user1Game.value.state, GameState.FLEET_SETUP)
        }
    }
    val user2Game = gameServices.getGame(user2GameIdResult.value)
    when (user2Game) {
        is Either.Left -> fail("Unexpected $user2Game")
        is Either.Right -> {
            Assertions.assertEquals(user2Game.value.id, user2GameIdResult.value)
            Assertions.assertEquals(user2Game.value.player1, player1)
            Assertions.assertEquals(user2Game.value.player2, player2)
            Assertions.assertEquals(user2Game.value.state, GameState.FLEET_SETUP)
        }
    }
    // asserts if both users are playing the same game
    val player1Game = user1Game.value
    val player2Game = user2Game.value
    Assertions.assertEquals(player1Game.id, player2Game.id)
    Assertions.assertEquals(player1Game.player1, player2Game.player1)
    Assertions.assertEquals(player1Game.player2, player2Game.player2)
    return user1GameIdResult.value
}

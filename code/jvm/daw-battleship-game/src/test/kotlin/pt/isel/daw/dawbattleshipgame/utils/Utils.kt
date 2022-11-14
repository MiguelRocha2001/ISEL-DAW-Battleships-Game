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
    boardSize = 10,
    fleet = setOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun getGameTestConfiguration2() = Configuration(
    boardSize = 10,
    fleet = setOf(
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.DESTROYER, 2)
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun getCreateGameInputModel() = CreateGameInputModel(
    boardSize = 10,
    fleet = mapOf(
        ShipTypeInputModel.CARRIER to 5,
        ShipTypeInputModel.BATTLESHIP to 4,
        ShipTypeInputModel.CRUISER to 3,
        ShipTypeInputModel.SUBMARINE to 3,
        ShipTypeInputModel.DESTROYER to 2
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun generateGameId(): Int = (Math.random() * 100000).toInt()

fun generateToken(): String = (Math.random() * 100000).toString()

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
fun createGame(transactionManager: TransactionManager, player1: Int, player2: Int, configuration: Configuration): Int {
    val gameService = GameServices(transactionManager)
    val gameCreationResult1 = gameService.startGame(player1, configuration)
    when (gameCreationResult1) {
        is Either.Left -> fail("Unexpected $gameCreationResult1")
        is Either.Right -> {
            Assertions.assertEquals(gameCreationResult1.value.first, GameState.NOT_STARTED)
            Assertions.assertNull(gameCreationResult1.value.second)
        }
    }
    val gameCreationResult2 = gameService.startGame(player2, configuration)
    when (gameCreationResult2) {
        is Either.Left -> fail("Unexpected $gameCreationResult2")
        is Either.Right -> {
            Assertions.assertEquals(gameCreationResult2.value.first, GameState.FLEET_SETUP)
            Assertions.assertNotNull(gameCreationResult2.value.second)
        }

    }
    val user1GameIdResult = gameService.getGameIdByUser(player1)
    when (user1GameIdResult) {
        is Either.Left -> fail("Unexpected $gameCreationResult1")
        is Either.Right -> Assertions.assertTrue(user1GameIdResult.value >= 0)
    }
    val user2GameIdResult = gameService.getGameIdByUser(player2)
    when (user2GameIdResult) {
        is Either.Left -> fail("Unexpected $gameCreationResult2")
        is Either.Right -> Assertions.assertTrue(user2GameIdResult.value >= 0)
    }
    Assertions.assertEquals(user1GameIdResult.value, user2GameIdResult.value)

    when (val user1Game = gameService.getGame(user1GameIdResult.value)) {
        is Either.Left -> fail("Unexpected $user1Game")
        is Either.Right -> {
            Assertions.assertEquals(user1Game.value.id, user1GameIdResult.value)
            Assertions.assertEquals(user1Game.value.player1, player1)
            Assertions.assertEquals(user1Game.value.player2, player2)
            Assertions.assertEquals(user1Game.value.state, GameState.FLEET_SETUP)
        }
    }
    when (val user2Game = gameService.getGame(user2GameIdResult.value)) {
        is Either.Left -> fail("Unexpected $user2Game")
        is Either.Right -> {
            Assertions.assertEquals(user2Game.value.id, user2GameIdResult.value)
            Assertions.assertEquals(user2Game.value.player1, player1)
            Assertions.assertEquals(user2Game.value.player2, player2)
            Assertions.assertEquals(user2Game.value.state, GameState.FLEET_SETUP)
        }
    }
    return user1GameIdResult.value
}

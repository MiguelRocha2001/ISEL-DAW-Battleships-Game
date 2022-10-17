package pt.isel.daw.dawbattleshipgame.domain.state.services

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.utils.Sha256TokenEncoder
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameServicesTests {
    val configuration = getGameTestConfiguration()

    @Test
    fun createGame() {
        testWithTransactionManagerAndRollback { transactionManager ->

            val userService = UserServices(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            // Create User 1
            val player1 = "user1"
            var createUserResult = userService.createUser(player1, "password1")
            // then: the creation is successful
            when (createUserResult) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
            }
            val player1Id = createUserResult.value.toInt()

            // Create User
            val player2 = "user2"
            createUserResult = userService.createUser(player2, "password2")
            // then: the creation is successful
            when (createUserResult) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
            }
            val player2Id = createUserResult.value.toInt()

            val gameServices = GameServices(transactionManager)
            // Create Game
            gameServices.startGame(player1Id, getGameTestConfiguration()) // player 1 will be put in a queue

            val game = gameServices.startGame(player2Id, getGameTestConfiguration())


        }
    }

    fun joinGame() {
        TODO()
    }

    fun getGame() {
        TODO()
    }

    fun makeMove() {
        TODO()
    }
}
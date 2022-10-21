package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.utils.Sha256TokenEncoder
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback
import java.util.*

class UserServiceTests {

    @Test
    fun `can create user, token, and retrieve by token`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UserServices(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // when: creating a user
            val createUserResult = userService.createUser("bob", "password1")

            // then: the creation is successful
            when (createUserResult) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
            }

            // when: creating a token
            val createTokenResult = userService.createToken("bob", "password2")

            // then: the creation is successful
            val token = when (createTokenResult) {
                is Either.Left -> Assertions.fail(createTokenResult.toString())
                is Either.Right -> createTokenResult.value
            }

            // and: the token bytes have the expected length
            val tokenBytes = Base64.getUrlDecoder().decode(token)
            assertEquals(256 / 8, tokenBytes.size)

            // when: retrieving the user by token
            val user = userService.getUserByToken(token)

            // then: a user is found
            assertNotNull(user)

            // and: has the expected name
            assertEquals("bob", user?.username)
        }
}
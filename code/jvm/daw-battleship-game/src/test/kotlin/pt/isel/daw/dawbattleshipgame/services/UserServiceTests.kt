package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.user.TokenCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserDeletionError
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.utils.*

class UserServiceTests {
    @Test
    fun create_user(){
        testWithTransactionManagerAndRollback{transactionManager ->
            val userService = UserServices(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            val result = userService.createUser("user", "Password") as Either.Right
            assertTrue(result.value > 0)
        }
    }

    @Test
    fun create_user_with_existing_username(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            userService.createUser("user", "Password") as Either.Right
            val result2 = userService.createUser("user", "passworD") as Either.Left
            assertEquals(UserCreationError.UserAlreadyExists, result2.value)
        }
    }

    @Test
    fun create_user_with_weak_password(){

        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            val result = userService.createUser("user", "weak") as Either.Left
            assertEquals(UserCreationError.InsecurePassword, result.value)
        }
    }
    @Test
    fun create_token(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            userService.createUser("user", "Password") as Either.Right
            val token = userService.createToken("user", "Password") as Either.Right
            assertNotNull(token.value)
            assertEquals(44, token.value.length)
        }
    }

    @Test
    fun create_token_fpr_bad_authenticated_user(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            userService.createUser("user", "Password") as Either.Right
            val token = userService.createToken("user", "Passsword") as Either.Left
            assertEquals(TokenCreationError.UserOrPasswordAreInvalid, token.value)
        }
    }

    @Test
    fun get_user_using_token(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            userService.createUser("user", "Password") as Either.Right
            val token = userService.createToken("user", "Password") as Either.Right
            val user = userService.getUserByToken(token.value)
            assertNotNull(user)
            assertEquals("user", user!!.username)
        }
    }

    @Test
    fun get_user_using_bad_token(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            userService.createUser("user", "Password") as Either.Right
            userService.createToken("user", "Password") as Either.Right
            val user = userService.getUserByToken("___________________________________________")
            assertNull(user)
        }
    }

    @Test
    fun delete_user() {
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            val willBeDeletedId = userService.createUser("user", "Password") as Either.Right
            val token = userService.createToken("user", "Password") as Either.Right
            userService.getUserByToken(token.value)


            userService.deleteUser(willBeDeletedId.value) as Either.Right
            val deletedUser = userService.getUserByToken(token.value)
            assertNull(deletedUser)
        }
    }
    @Test
    fun delete_invalid_user(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            val result = userService.deleteUser(-1) as Either.Left
            assertEquals(UserDeletionError.UserDoesNotExist, result.value)
        }
    }

    @Test
    fun rankings_of_users_with_no_games(){
        testWithTransactionManagerAndRollback {
            val userService = UserServices(
                it,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )
            val userPair = createUserPair(it)
            val gameServices = GameServices(it)
            createGame(gameServices, userPair.first, userPair.second, getGameTestConfiguration1())

            val rankings = userService.getUserRanking()
            println(rankings)
            assertEquals(2, rankings.size)
            assertEquals(0, rankings[0].wins)
            assertTrue(rankings.map { u -> u.username }.containsAll(listOf("user1", "user2")))
        }
    }
}
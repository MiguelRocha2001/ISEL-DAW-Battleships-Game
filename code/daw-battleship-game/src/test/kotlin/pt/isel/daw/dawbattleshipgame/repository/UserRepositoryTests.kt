package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class UserRepositoryTests {
    private val passwordEncoder = BCryptPasswordEncoder()
    @Test
    fun `can create and retrieve`(): Unit = testWithTransactionManagerAndRollback { transactionManager ->
        transactionManager.run { transaction ->
            // given: repositories and logic
            val userRepo = transaction.usersRepository
            val username = "user"
            val passwordValidationInfo = PasswordValidationInfo(passwordEncoder.encode("user1password"))

            userRepo.storeUser(username, passwordValidationInfo)

            val user = userRepo.getUserByUsername(username)
            assertEquals(username, user?.username)
            assertEquals(passwordValidationInfo, user?.passwordValidation)
        }
    }


}
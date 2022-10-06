package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback

class GameRepositoryTests {
    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun `can create and retrieve`(): Unit = testWithHandleAndRollback { handle ->
        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val username = "user1"
        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode("password1")
        )
        userRepo.storeUser(username, passwordValidationInfo)
    }
}
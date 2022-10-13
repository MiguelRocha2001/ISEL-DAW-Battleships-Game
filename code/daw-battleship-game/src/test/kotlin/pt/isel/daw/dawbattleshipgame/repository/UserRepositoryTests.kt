package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.utils.generateToken
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class UserRepositoryTests {

    @Test
    fun `can create and retrieve`(): Unit = testWithTransactionManagerAndRollback { transactionManager ->
        transactionManager.run { transaction ->
            // given: repositories and logic
            val userRepo = transaction.usersRepository
            val userId = generateToken()
            val username = "user1"
            val hashedPassword = "password1".hashCode().toString()

            userRepo.storeUser(userId, username, hashedPassword)

            val user = userRepo.getUserByUsername(username)
            assertEquals(userId, user?.id)
            assertEquals(username, user?.username)
            assertEquals(hashedPassword, user?.hashedPassword)
        }
    }


}
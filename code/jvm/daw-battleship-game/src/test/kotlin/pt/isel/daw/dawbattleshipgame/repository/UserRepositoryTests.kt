package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.game.InitGame
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.clearAllTables
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback
import java.lang.Thread.sleep

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

    @Test
    fun `check user stats`() = testWithTransactionManagerAndRollback { transactionManager ->
        transactionManager.run { tr ->
            tr.gamesRepository.emptyRepository()
            tr.usersRepository.clearAll()
            sleep(1000)
            val userRepo = tr.usersRepository
            val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("user1password"))
            val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("user2password"))
            userRepo.storeUser("user1", passwordValidationInfo1)
            userRepo.storeUser("user2", passwordValidationInfo2)

            val gamesRepo = tr.gamesRepository
            gamesRepo.startGame(InitGame(userRepo.getUserByUsername("user1")!!.id,
                    userRepo.getUserByUsername("user2")!!.id, getGameTestConfiguration1()))

            val ur = tr.usersRepository.getUsersRanking()
            println(ur)
            val user1 = ur.first{it.username == "user1"}
            val user2 = ur.first{it.username == "user1"}
            assert(user1.gamesPlayed == 1 && user2.gamesPlayed == 1)
            assert(user1.wins == 0 && user2.wins == 0)

        }
    }
}
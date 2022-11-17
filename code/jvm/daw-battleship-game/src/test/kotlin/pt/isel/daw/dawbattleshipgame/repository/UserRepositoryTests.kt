package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.game.InitGame
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.clearAllTables
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1
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

    @Test
    fun `check user stats`() = testWithTransactionManagerAndRollback { transactionManager ->
        transactionManager.run { tr ->
            val userRepo = tr.usersRepository
            val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("user1password"))
            val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("user2password"))
            userRepo.storeUser("user1", passwordValidationInfo1)
            userRepo.storeUser("user2", passwordValidationInfo2)
            val ur = tr.usersRepository.getUsersRanking()
            assert(ur.size == 2)
            val one = ur.first()
            assert(one.gamesPlayed == 0)
            assert(one.wins == 0)
            println(ur)

            val gamesRepo = tr.gamesRepository
            gamesRepo.startGame(InitGame(userRepo.getUserByUsername("user1")!!.id,
                    userRepo.getUserByUsername("user2")!!.id, getGameTestConfiguration1()))

            val ur2 = tr.usersRepository.getUsersRanking()
            assert(ur2.size == 2)
            val first = ur2.first()
            assert(first.gamesPlayed == 1)
            assert(first.wins == 0)
            println(ur2)

        }
    }


}
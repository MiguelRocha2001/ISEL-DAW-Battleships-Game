package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.repository.jdbi.users.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.*

class GameRepositoryTests {
    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun testConnection() {
        testWithHandleAndRollback { handle ->
            JdbiUsersRepository(handle)
        }
    }

    @Test
    fun testCreateGame_NewUserStats() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("User1password"))
                val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("User2password"))
                val usersRepo = transaction.usersRepository
                val player1Id = usersRepo.storeUser("user1", passwordValidationInfo1)
                val player2Id = usersRepo.storeUser("user2", passwordValidationInfo2)

                val gamesRepo = transaction.gamesRepository
                resetGamesDatabase(gamesRepo) // clears all games
                val gameId = generateGameId()
                val configuration = getGameTestConfiguration1()
                val instant = RealClock.now()
                val game = Game.newGame(gameId, player1Id, player2Id, configuration, instant) // PreparationPhase

                gamesRepo.saveGame(game)

                val gameFromDb = gamesRepo.getGame(gameId)

                checkNotNull(gameFromDb)
                assert(gameFromDb.id == gameId)
                assert(gameFromDb.configuration == configuration)
                assert(gameFromDb.player1 == player1Id)
                assert(gameFromDb.player2 == player2Id)
                assert(gameFromDb.state == GameState.FLEET_SETUP)
                assert(gameFromDb.instants.created == instant)
                assert(gameFromDb.instants.updated == instant)
            }
        }
    }

    @Test
    fun testNewCreateGame() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("User1password"))
                val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("User2password"))
                val usersRepo = transaction.usersRepository
                val player1Id = usersRepo.storeUser("user1", passwordValidationInfo1)
                val player2Id = usersRepo.storeUser("user2", passwordValidationInfo2)
                val gamesRepo = transaction.gamesRepository
                resetGamesDatabase(gamesRepo) // clears all games
                val configuration = getGameTestConfiguration1()
                val game = Game.startGame(player1Id, player2Id, configuration) // PreparationPhase
                val gameId = gamesRepo.startGame(game)
                requireNotNull(gameId)
                val gameFromDb = gamesRepo.getGame(gameId)
                checkNotNull(gameFromDb)
                assert(gameFromDb.id == gameId)
                assert(gameFromDb.configuration == configuration)
                assert(gameFromDb.player1 == player1Id)
                assert(gameFromDb.player2 == player2Id)
                assert(gameFromDb.state == GameState.FLEET_SETUP)
            }
        }
    }

    @Test
    fun testCreateGameNewUserStats() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("User1password"))
                val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("User2password"))
                val usersRepo = transaction.usersRepository
                val player1Id = usersRepo.storeUser("user1", passwordValidationInfo1)
                val player2Id = usersRepo.storeUser("user2", passwordValidationInfo2)

                val gamesRepo = transaction.gamesRepository
                resetGamesDatabase(gamesRepo) // clears all games
                val gameId = generateGameId()
                val configuration = getGameTestConfiguration1()
                val instant = RealClock.now()
                val game = Game.newGame(gameId, player1Id, player2Id, configuration, instant) // PreparationPhase

                gamesRepo.saveGame(game)

                val gameFromDb = gamesRepo.getGame(gameId)

                checkNotNull(gameFromDb)
                assert(gameFromDb.id == gameId)
                assert(gameFromDb.configuration == configuration)
                assert(gameFromDb.player1 == player1Id)
                assert(gameFromDb.player2 == player2Id)
                assert(gameFromDb.state == GameState.FLEET_SETUP)
                assert(gameFromDb.instants.created == instant)
                assert(gameFromDb.instants.updated == instant)

                val user1Updated = usersRepo.getUserByUsername("user1")!!
                val user2Updated = usersRepo.getUserByUsername("user2")!!

                assert(user1Updated.gamesPlayed == 1 && user1Updated.wins == 0)
                assert(user2Updated.gamesPlayed == 1 && user2Updated.wins == 0)
            }
        }
    }
}
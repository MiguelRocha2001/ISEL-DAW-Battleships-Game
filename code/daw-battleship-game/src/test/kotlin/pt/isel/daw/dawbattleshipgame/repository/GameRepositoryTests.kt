package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
=======
>>>>>>> Stashed changes
import pt.isel.daw.dawbattleshipgame.domain.state.utils.generateGameId
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.repository.jdbi.users.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameRepositoryTests {
    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun testConnection() {
        testWithHandleAndRollback { handle ->
            JdbiUsersRepository(handle)
        }
    }

    @Test
    fun testCreateGame() {
        testWithTransactionManagerAndRollback { transactionManager ->
            transactionManager.run { transaction ->
                val passwordValidationInfo1 = PasswordValidationInfo(passwordEncoder.encode("user1password"))
                val passwordValidationInfo2 = PasswordValidationInfo(passwordEncoder.encode("user1password"))
                val usersRepo = transaction.usersRepository
                usersRepo.storeUser("user1", passwordValidationInfo1)
                usersRepo.storeUser("user2", passwordValidationInfo2)

                val gamesRepo = transaction.gamesRepository
                resetGamesDatabase(gamesRepo) // clears all games
                val gameId = generateGameId()
                val player1Id = generateRandomId()
                val player2Id = generateRandomId()
                val configuration = getGameTestConfiguration()
                val game = Game.newGame(gameId, player1Id, player2Id, configuration) // PreparationPhase

                gamesRepo.saveGame(game)

                val gameFromDb = gamesRepo.getGame(gameId)

                checkNotNull(gameFromDb)
                assert(gameFromDb.gameId == gameId)
                assert(gameFromDb.configuration == configuration)
                assert(gameFromDb.player1 == player1Id)
                assert(gameFromDb.player2 == player2Id)
                assert(gameFromDb is SinglePhase)
                (gameFromDb as SinglePhase).let { preparationPhase ->
                    (preparationPhase.player1Game).let {
<<<<<<< Updated upstream
                        val player1PreparationPhase = it as PlayerPreparationPhase
=======
                        val player1PreparationPhase = it
>>>>>>> Stashed changes
                        assert(player1PreparationPhase.playerId == player1Id)
                        assert(player1PreparationPhase.board.toString() ==  player1PreparationPhase.board.toString())
                    }
                    (preparationPhase.player2Game).let {
<<<<<<< Updated upstream
                        val player2PreparationPhase = it as PlayerPreparationPhase
=======
                        val player2PreparationPhase = it
>>>>>>> Stashed changes
                        assert(player2PreparationPhase.playerId == player2Id)
                        assert(player2PreparationPhase.board.toString() == player2PreparationPhase.board.toString())
                    }
                }
            }
        }
    }
}
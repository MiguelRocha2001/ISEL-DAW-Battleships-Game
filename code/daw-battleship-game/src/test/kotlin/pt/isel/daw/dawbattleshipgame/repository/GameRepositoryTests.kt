package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.PreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.game.utils.generateGameId
import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManager
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
                val usersRepo = transaction.usersRepository
                usersRepo.storeUser("user1", "user1", passwordEncoder.encode("password1"))
                usersRepo.storeUser("user2", "user2", passwordEncoder.encode("password2"))

                val gamesRepo = transaction.gamesRepository
                resetGamesDatabase(gamesRepo) // clears all games
                val gameId = generateGameId()
                val configuration = getGameTestConfiguration()
                val game = Game.newGame(gameId, "user1", "user2", configuration) // PreparationPhase

                gamesRepo.saveGame(game)

                val gameFromDb = gamesRepo.getGame(gameId)

                checkNotNull(gameFromDb)
                assert(gameFromDb.gameId == gameId)
                assert(gameFromDb.configuration == configuration)
                assert(gameFromDb.player1 == "user1")
                assert(gameFromDb.player2 == "user2")
                assert(gameFromDb is PreparationPhase)
                (gameFromDb as PreparationPhase).let { preparationPhase ->
                    (preparationPhase.player1PreparationPhase).let { player1PreparationPhase ->
                        assert(player1PreparationPhase.playerId == "user1")
                        assert(player1PreparationPhase.board.toString() ==  game.player1PreparationPhase.board.toString())
                    }
                    (preparationPhase.player2PreparationPhase).let { player2PreparationPhase ->
                        assert(player2PreparationPhase.playerId == "user2")
                        assert(player2PreparationPhase.board.toString() == game.player2PreparationPhase.board.toString())
                    }
                }
            }
        }
    }
}
package pt.isel.daw.dawbattleshipgame.repository

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.utils.generateGameId
import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback

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
        testWithHandleAndRollback { handle ->
            // resetDatabase()
            val usersRepo = JdbiUsersRepository(handle)
            usersRepo.storeUser("user1", "user1", passwordEncoder.encode("password1"))
            usersRepo.storeUser("user2", "user2", passwordEncoder.encode("password2"))
            val gamesRepo = JdbiGamesRepository(handle)
            val gameId = generateGameId()
            val configuration = getGameTestConfiguration()
            val game = Game.newGame(gameId, "user1", "user2", configuration) // PreparationPhase

            gamesRepo.saveGame(game)
        }
    }
}
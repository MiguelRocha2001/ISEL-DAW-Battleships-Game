package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.utils.createGame
import pt.isel.daw.dawbattleshipgame.utils.createUserPair
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManagerAndRollback

class GameEndServicesTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun quitGameTest() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameServices = GameServices(transactionManager)

            val userPair = createUserPair(transactionManager)

            val gameId = createGame(gameServices, userPair.first, userPair.second, configuration)

            gameServices.quitGame(userPair.first, gameId)

            val game = gameServices.getGame(gameId) ?: fail { "Game not found" }
            assert(game.state == GameState.FINISHED)
            assert(game.winner == userPair.second)
        }
    }
}
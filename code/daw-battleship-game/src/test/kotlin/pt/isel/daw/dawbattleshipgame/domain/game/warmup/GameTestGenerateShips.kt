package pt.isel.daw.dawbattleshipgame.domain.game.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.utils.generateToken

import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.generateGameId

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {
        val gameId = generateGameId()
        val player1 = generateToken()
        val player2 = generateToken()
        val configuration = getGameTestConfiguration()
        val game = Game.newGame(gameId, player1, player2, configuration)
        val player1PreparationPhase = game.player1PreparationPhase
        val g2 = player1PreparationPhase // TODO
    }
}
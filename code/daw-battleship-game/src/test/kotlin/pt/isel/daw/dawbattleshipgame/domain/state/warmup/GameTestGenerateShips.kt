package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.state.Game

import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {//FIXME
        val gameId = generateRandomId()
        val player1 = generateRandomId()
        val player2 = generateRandomId()
        val configuration = getGameTestConfiguration()
        val game = Game.newGame(gameId, player1, player2, configuration)
        val player1PreparationPhase = game.player1Game
        val g2 = player1PreparationPhase
    }
}
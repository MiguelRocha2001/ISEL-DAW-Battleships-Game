package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.utils.generateId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {//FIXME
        val gameId = generateId()
        val player1 = generateId()
        val player2 = generateId()
        val configuration = getGameTestConfiguration()
        val game = Game.newGame(gameId, player1, player2, configuration)
        val player1PreparationPhase = game.board1
        val g2 = player1PreparationPhase
    }
}
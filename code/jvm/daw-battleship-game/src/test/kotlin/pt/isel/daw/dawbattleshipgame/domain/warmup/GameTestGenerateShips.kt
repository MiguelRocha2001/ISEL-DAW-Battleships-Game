package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.generateShips
import pt.isel.daw.dawbattleshipgame.utils.generateId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {
        val gameId = generateId()
        val player1 = generateId()
        val player2 = generateId()
        val configuration = getGameTestConfiguration()
        val game = Game.newGame(gameId, player1, player2, configuration)
        val g2 = game.generateShips()
        println("\n:Board 1:\n" + g2.board1)
        println("\n:Board 2:\n" + g2.board2)
        assertTrue(g2.allShipsPlaced())
    }
}
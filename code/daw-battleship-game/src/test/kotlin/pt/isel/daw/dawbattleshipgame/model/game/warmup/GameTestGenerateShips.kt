package pt.isel.daw.dawbattleshipgame.model.game.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.model.game.Game

import pt.isel.daw.dawbattleshipgame.model.game.utils.getGameTestConfiguration

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {
        val game = Game.newGame(getGameTestConfiguration())
        val g2 = game.generateShips()
        println(g2.toString())
    }
}
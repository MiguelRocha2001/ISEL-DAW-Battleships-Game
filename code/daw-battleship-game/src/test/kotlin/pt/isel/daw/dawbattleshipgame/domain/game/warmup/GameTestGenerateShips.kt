package pt.isel.daw.dawbattleshipgame.domain.game.warmup

import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.Player
import pt.isel.daw.dawbattleshipgame.domain.game.Game

import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {
        val game = Game.newGame(getGameTestConfiguration(), Player.Player1)
        val g2 = game.generateShips()
        println(g2.toString())
    }
}
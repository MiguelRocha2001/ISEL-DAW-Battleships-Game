package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.moveShip
import pt.isel.daw.dawbattleshipgame.domain.game.placeShip
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.*


class BoardWithDifferentSizes {
    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private var configuration = getGameTestConfiguration1()

    @Test
    fun config_15_size() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  | K  | L  | M  | N  | O  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 9 |    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 10|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 11|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 12|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 13|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 14|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 15|    |    |    |    |    |    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun config_10_size() {
        configuration = getGameTestConfiguration2()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }


    @Test
    fun config_8_size() {
        configuration = getGameTestConfiguration3()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun config_13_size() {
        configuration = getGameTestConfiguration5()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  | K  | L  | M  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 9 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 10|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 11|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 12|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 13|    |    |    |    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

}
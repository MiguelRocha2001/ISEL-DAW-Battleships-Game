package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.moveShip
import pt.isel.daw.dawbattleshipgame.domain.game.placeShip
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration1


class GameTestMoving {
    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private val configuration = getGameTestConfiguration1()

    @Test
    fun valid_ship_move_1() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
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
    fun valid_ship_move_2() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("B1".toCoordinate(), "B4".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 | [] | [] |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_3() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("B1".toCoordinate(), "J1".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    | [] | [] |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_4() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "A1".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_5() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("B1".toCoordinate(), "B1".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_6() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.moveShip("D2".toCoordinate(), "D6".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun invalid_ship_move_1() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("B1".toCoordinate(), "A1".toCoordinate())
        assertEquals(null, gameResult)
    }

    @Test
    fun invalid_ship_move_2() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "J8".toCoordinate())
        assertEquals(null, gameResult)
    }
}
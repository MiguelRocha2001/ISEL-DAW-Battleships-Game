package pt.isel.daw.dawbattleshipgame.domain.game.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.game.utils.generateToken
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId


class GameTestPlacing {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun initializing_new_game() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_1() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] | [] |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_2() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A2".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_3() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "E8".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    | [] | [] |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }


    @Test
    fun placing_ship_on_invalid_location_1() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "j1".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
    }

    @Test
    fun placing_ship_on_invalid_location_2() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "J10".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
    }
}
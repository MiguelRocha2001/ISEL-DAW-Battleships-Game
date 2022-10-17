package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId


class GameTestMultipleActions {

    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun adding_two_ships_and_rotate_both() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryRotateShip("H8".toCoordinate())
        gameResult = gameResult?.tryRotateShip("D1".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    | [] | [] | [] |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    | [] | [] |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun adding_two_ships_and_rotate_one() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryRotateShip("D1".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    | [] | [] | [] |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    | [] |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    | [] |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun adding_two_ships_rotate_both_but_one_is_invalid() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "H10".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryRotateShip("D1".toCoordinate())
        gameResult = gameResult?.tryRotateShip("H10".toCoordinate())
        assertNull(gameResult)
    }


    @Test
    fun moving_a_ship_then_place_one_near() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryMoveShip("D1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER,"A1".toCoordinate(),Orientation.VERTICAL)
        assertNull(gameResult)
    }

    @Test
    fun move_a_ship_overlapping_the_rotation_of_other() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A10".toCoordinate(),"F1".toCoordinate())
        val gameResult2 = gameResult?.tryRotateShip("D1".toCoordinate())
        assertNotNull(gameResult)
        assertNull(gameResult2)
    }

    @Test
    fun move_a_ship_to_the_radius_of_the_rotation_of_the_other() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.BATTLESHIP, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A10".toCoordinate(),"F3".toCoordinate())
        val gameResult2 = gameResult?.tryRotateShip("D1".toCoordinate())
        assertNotNull(gameResult)
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    | [] | [] | [] | [] |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    | [] | [] |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult2.toString()
        )
    }
    @Test
    fun place_move_and_rotate_multiple_ships() {//FIXME  corrgir barcos a sobreporem-se apos rotação
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.CARRIER, "A3".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.DESTROYER, "A5".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.CRUISER, "A7".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryPlaceShip(ShipType.BATTLESHIP, "A9".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.tryMoveShip("A3".toCoordinate(),"F3".toCoordinate())
        gameResult = gameResult?.tryMoveShip("A5".toCoordinate(),"C5".toCoordinate())
        gameResult = gameResult?.tryMoveShip("A7".toCoordinate(),"B6".toCoordinate())
        gameResult = gameResult?.tryRotateShip("F3".toCoordinate())
        gameResult = gameResult?.tryRotateShip("A2".toCoordinate())
        assertNull(gameResult)

        println(gameResult)

    }

}
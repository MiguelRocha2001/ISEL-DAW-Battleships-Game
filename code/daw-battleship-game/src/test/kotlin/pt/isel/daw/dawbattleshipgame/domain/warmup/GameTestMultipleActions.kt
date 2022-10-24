package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration


class GameTestMultipleActions {

    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun adding_two_ships_and_rotate_both() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryRotateShip("H8".toCoordinate())
        gameResult = gameResult?.logic?.tryRotateShip("D1".toCoordinate())
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
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryRotateShip("D1".toCoordinate())
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
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "H10".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryRotateShip("D1".toCoordinate())
        gameResult = gameResult?.logic?.tryRotateShip("H10".toCoordinate())
        assertNull(gameResult)
    }


    @Test
    fun moving_a_ship_then_place_one_near() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryMoveShip("D1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER,"A1".toCoordinate(),Orientation.VERTICAL)
        assertNull(gameResult)
    }

    @Test
    fun move_a_ship_overlapping_the_rotation_of_other() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A10".toCoordinate(),"F1".toCoordinate())
        val gameResult2 = gameResult?.logic?.tryRotateShip("D1".toCoordinate())
        assertNotNull(gameResult)
        assertNull(gameResult2)
    }

    @Test
    fun move_a_ship_to_the_radius_of_the_rotation_of_the_other() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A10".toCoordinate(),"F3".toCoordinate())
        val gameResult2 = gameResult?.logic?.tryRotateShip("D1".toCoordinate())
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
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.CARRIER, "A3".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.DESTROYER, "A5".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.CRUISER, "A7".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "A9".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.logic?.tryMoveShip("A3".toCoordinate(),"F3".toCoordinate())
        gameResult = gameResult?.logic?.tryMoveShip("A5".toCoordinate(),"C5".toCoordinate())
        gameResult = gameResult?.logic?.tryMoveShip("A7".toCoordinate(),"B6".toCoordinate())
        gameResult = gameResult?.logic?.tryRotateShip("F3".toCoordinate())
        gameResult = gameResult?.logic?.tryRotateShip("A2".toCoordinate())
        assertNull(gameResult)

        println(gameResult)

    }

}
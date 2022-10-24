package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId


class GameTestRotation {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun rotating_ship_on_valid_location_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("c2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game3.toString()
        )
    }
    @Test
    fun rotating_ship_on_valid_location_2() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("d2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game3.toString()
        )
    }

    @Test
    fun rotating_empty_coordinate_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("c3".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_2() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("d3".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_3() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("b2".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_4() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryRotateShip("c1".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_ship_and_colliding_with_another_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.CARRIER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryPlaceShip(ShipType.DESTROYER, "C4".toCoordinate(), Orientation.HORIZONTAL)
        val game4 = game3?.logic?.tryRotateShip("c2".toCoordinate())
        assertNull(game4)
    }

    // TODO -> see this tests
    fun rotating_ship_and_colliding_with_another_2() {
        /*
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1PreparationPhase
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryPlaceShip("c4")
        val game4 = game3?.logic?.tryRotateShip("d2".toCoordinate())
        assertNull(game4)
         */
    }

    @Test
    fun rotating_ship_and_not_colliding_with_another_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "c2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.logic?.tryPlaceShip(ShipType.CARRIER, "c5".toCoordinate(), Orientation.HORIZONTAL)
        val game4 = game3?.logic?.tryRotateShip("c2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    | [] | [] | [] | [] | [] |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game4.toString()
        )
    }

    @Test
    fun rotating_ship_and_colliding_with_wall() {
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1Game 
            val game2 = game1.logic.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
            val game3 = game2?.logic?.tryRotateShip("A10".toCoordinate())
            assertNull(game3)
        }


}
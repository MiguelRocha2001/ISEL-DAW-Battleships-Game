package pt.isel.daw.dawbattleshipgame.game.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.model.Orientation
import pt.isel.daw.dawbattleshipgame.model.game.Game
import pt.isel.daw.dawbattleshipgame.model.ship.ShipType
import pt.isel.daw.dawbattleshipgame.model.toCoordinate


class GameTestRotation {
    private val gameConfig = getGameTestConfiguration()

    @Test
    fun rotating_ship_on_valid_location_1() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val game3 = game2?.tryRotateShip("c2")
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
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val game3 = game2?.tryRotateShip("d2")
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
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryRotateShip("c3")
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_2() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryRotateShip("d3")
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_3() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryRotateShip("b2")
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_4() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryRotateShip("c1")
        assertNull(game3)
    }

    @Test
    fun rotating_ship_and_colliding_with_another_1() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryPlaceShip("c4")
        val game4 = game3?.tryRotateShip("c2")
        assertNull(game4)
    }

    @Test
    fun rotating_ship_and_colliding_with_another_2() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip("c2")
        val game3 = game2?.tryPlaceShip("c4")
        val game4 = game3?.tryRotateShip("d2")
        assertNull(game4)
    }

    @Test
    fun rotating_ship_and_not_colliding_with_another_1() {
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip(ShipType.DESTROYER, "c2".toCoordinate()!!, Orientation.HORIZONTAL)
        val game3 = game2?.tryPlaceShip(ShipType.CARRIER, "c5".toCoordinate()!!, Orientation.HORIZONTAL)
        val game4 = game3?.tryRotateShip("c2")
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
        val game1 = Game.newGame(gameConfig)
        val game2 = game1.tryPlaceShip(ShipType.DESTROYER, "A10".toCoordinate()!!, Orientation.HORIZONTAL)
        val game3 = game2?.tryRotateShip("A10")
        assertNull(game3)
    }
}
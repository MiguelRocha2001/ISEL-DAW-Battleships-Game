package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.moveShip
import pt.isel.daw.dawbattleshipgame.domain.game.placeShip
import pt.isel.daw.dawbattleshipgame.domain.game.rotateShip
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration4


class GameTestMultipleActions {

    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private val configuration = getGameTestConfiguration4()

    @Test
    fun adding_two_ships_and_rotate_both() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.rotateShip("H8".toCoordinate())
        gameResult = gameResult?.rotateShip("D1".toCoordinate())
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
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun adding_two_ships_and_rotate_one() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "H8".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.rotateShip("D1".toCoordinate())
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
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun adding_two_ships_rotate_both_but_one_is_invalid() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "H10".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.rotateShip("D1".toCoordinate())
        gameResult = gameResult?.rotateShip("H10".toCoordinate())
        assertNull(gameResult)
    }


    @Test
    fun moving_a_ship_then_place_one_near() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.moveShip("D1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.placeShip(ShipType.DESTROYER,"A1".toCoordinate(),Orientation.VERTICAL)
        assertNull(gameResult)
    }

    @Test
    fun move_a_ship_overlapping_the_rotation_of_other() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A10".toCoordinate(),"F1".toCoordinate())
        val gameResult2 = gameResult?.rotateShip("D1".toCoordinate())
        assertNotNull(gameResult)
        assertNull(gameResult2)
    }

    @Test
    fun move_a_ship_to_the_radius_of_the_rotation_of_the_other() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.BATTLESHIP, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A10".toCoordinate(),"F3".toCoordinate())
        val gameResult2 = gameResult?.rotateShip("D1".toCoordinate())
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
            gameResult2?.getBoard().toString()
        )
    }
    @Test
    fun place_move_and_rotate_multiple_ships() {//FIXME  corrgir barcos a sobreporem-se apos rotação
        val game = Game.newGame(gameId, player1, player2, configuration) 
        var gameResult = game.placeShip(ShipType.SUBMARINE, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.placeShip(ShipType.CARRIER, "A3".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.placeShip(ShipType.DESTROYER, "A5".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.placeShip(ShipType.CRUISER, "A7".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.placeShip(ShipType.BATTLESHIP, "A9".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(),"A2".toCoordinate())
        gameResult = gameResult?.moveShip("A3".toCoordinate(),"F3".toCoordinate())
        gameResult = gameResult?.moveShip("A5".toCoordinate(),"C5".toCoordinate())
        gameResult = gameResult?.moveShip("A7".toCoordinate(),"B6".toCoordinate())
        gameResult = gameResult?.rotateShip("F3".toCoordinate())
        gameResult = gameResult?.rotateShip("A2".toCoordinate())
        assertNull(gameResult)

        println(gameResult)

    }

    @Test
    fun place_rotate_ship_and_move() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        val game1Result = game.placeShip(ShipType.BATTLESHIP, Coordinate(2,3), Orientation.VERTICAL) ?: fail("Should have placed ship")
        val game2Result = game1Result?.rotateShip(Coordinate(2,3)) ?: fail("Should have rotated ship")
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] | [] | [] | [] |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game2Result?.getBoard().toString()
        )

        game2Result.getBoard().isShip(Coordinate(2,3))
        game2Result.getBoard().isShip(Coordinate(2,4))
        game2Result.getBoard().isShip(Coordinate(2,5))
        game2Result.getBoard().isShip(Coordinate(2,6))
        println(game2Result.toString())

        val game3Result = game2Result.moveShip(Coordinate(2,3), Coordinate(3,3)) ?: fail("Should have moved ship")
        game3Result.getBoard().isShip(Coordinate(3,3))
        game3Result.getBoard().isShip(Coordinate(3,4))
        game3Result.getBoard().isShip(Coordinate(3,5))
        game3Result.getBoard().isShip(Coordinate(3,6))

    }

}
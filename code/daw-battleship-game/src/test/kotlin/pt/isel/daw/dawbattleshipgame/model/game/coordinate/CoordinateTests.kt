package pt.isel.daw.dawbattleshipgame.model.game.coordinate

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import pt.isel.daw.dawbattleshipgame.model.Coordinate
import pt.isel.daw.dawbattleshipgame.model.Coordinates

internal class CoordinateTest {

    @Test
    fun radius_of_board_size_10() {
        val coordinate = Coordinate(2, 2)
        val radius = Coordinates(10).radius(coordinate)
        val expected = listOf(
            Coordinate(1, 1),
            Coordinate(1, 2),
            Coordinate(1, 3),
            Coordinate(2, 1),
            Coordinate(2, 3),
            Coordinate(3, 1),
            Coordinate(3, 2),
            Coordinate(3, 3),
        )


        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, expected.size)
    }

    @Test
    fun radius_of_board_size_10_with_piece_in_up_corner() {
        val coordinate = Coordinate(1, 1)
        val radius = Coordinates(10).radius(coordinate)
        val expected = listOf(
            Coordinate(1, 2),
            Coordinate(2, 1),
            Coordinate(2, 2),
        )

        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, expected.size)
    }
}
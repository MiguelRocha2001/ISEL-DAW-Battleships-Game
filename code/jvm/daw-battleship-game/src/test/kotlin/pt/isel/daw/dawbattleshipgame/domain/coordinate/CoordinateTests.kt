package pt.isel.daw.dawbattleshipgame.domain.coordinate

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinates
import pt.isel.daw.dawbattleshipgame.http.ApiException

internal class CoordinateTest {

    @Test
    fun creation_of_invalid_coordinate() {
        val thrown: ApiException = assertThrows(ApiException::class.java) {
            Coordinate(-2, 4)
        }

        assertTrue(thrown.message == "Row must be greater then 0")

    }

    @Test
    fun valid_down_coordinate() {
        val coordinate = Coordinate(1, 3)
        val newCoordinate = Coordinates(10).down(coordinate)
        val expected =  Coordinate(2,3)

        assertEquals(newCoordinate,expected)
    }

    @Test
    fun invalid_down_coordinate() {
        val coordinate = Coordinate(1, 3)
        val newCoordinate = Coordinates(10).down(coordinate,10)

        assertNull(newCoordinate)
    }

    @Test
    fun invalid_down_coordinate_2() {
        val coordinate = Coordinate(10, 3)
        val newCoordinate = Coordinates(10).down(coordinate)

        assertNull(newCoordinate)
    }


    @Test
    fun valid_left_coordinate() {
        val coordinate = Coordinate(1, 3)
        val newCoordinate = Coordinates(10).left(coordinate)
        val expected =  Coordinate(1,2)

        assertEquals(newCoordinate,expected)
    }

    @Test
    fun invalid_left_coordinate_on_edge() {
        val coordinate = Coordinate(3, 4)
        val newCoordinate = Coordinates(10).left(coordinate,10)

        assertNull(newCoordinate)
    }

    @Test
    fun invalid_left_coordinate_big_amount() {
        val coordinate = Coordinate(3, 1)
        val newCoordinate = Coordinates(10).left(coordinate,10)

        assertNull(newCoordinate)
    }

    @Test
    fun valid_right_coordinate() {
        val coordinate = Coordinate(1, 3)
        val newCoordinate = Coordinates(10).right(coordinate)
        val expected =  Coordinate(1,4)

        assertEquals(newCoordinate,expected)
    }

    @Test
    fun invalid_right_coordinate_big_amount() {
        val coordinate = Coordinate(5, 5)
        val newCoordinate = Coordinates(10).right(coordinate,10)

        assertNull(newCoordinate)
    }

    @Test
    fun invalid_right_coordinate_on_edge() {
        val coordinate = Coordinate(1, 10)
        val newCoordinate = Coordinates(10).right(coordinate)

        assertNull(newCoordinate)
    }

    @Test
    fun valid_up_coordinate_big_amount() {
        val coordinate = Coordinate(3, 3)
        val newCoordinate = Coordinates(10).up(coordinate)
        val expected =  Coordinate(2,3)

        assertEquals(newCoordinate,expected)
    }

    @Test
    fun invalid_up_coordinate_big_amount() {
        val coordinate = Coordinate(4, 5)
        val newCoordinate = Coordinates(10).up(coordinate,10)

        assertNull(newCoordinate)
    }

    @Test
    fun invalid_up_coordinate_on_edge() {
        val coordinate = Coordinate(1, 4)
        val newCoordinate = Coordinates(10).up(coordinate)

        assertNull(newCoordinate)
    }


    @Test
    fun invalid_negative_movement_coordinate() {
        val coordinate = Coordinate(1, 3)
        val newCoordinateDown = Coordinates(10).down(coordinate,-10)
        val newCoordinateLeft = Coordinates(10).left(coordinate,-10)
        val newCoordinateRight = Coordinates(10).right(coordinate,-10)
        val newCoordinateUp = Coordinates(10).up(coordinate,-10)

        assertNull(newCoordinateDown)
        assertNull(newCoordinateLeft)
        assertNull(newCoordinateRight)
        assertNull(newCoordinateUp)
    }

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

    @Test
    fun radius_of_board_size_10_with_piece_on_the_side() {
        val coordinate = Coordinate(5, 1)
        val radius = Coordinates(10).radius(coordinate)
        val expected = listOf(
            Coordinate(4, 1),
            Coordinate(4, 2),
            Coordinate(5, 2),
            Coordinate(6, 1),
            Coordinate(6, 2),
        )

        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, expected.size)
    }

    @Test
    fun radius_of_board_size_10_with_piece_on_the_bottom() {
        val coordinate = Coordinate(10, 5)
        val radius = Coordinates(10).radius(coordinate)
        val expected = listOf(
            Coordinate(10, 6),
            Coordinate(10, 4),
            Coordinate(9, 4),
            Coordinate(9, 5),
            Coordinate(9, 6),
        )

        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, expected.size)
    }

    @Test
    fun radius_on_a_small_board() {
        val coordinate = Coordinate(2, 2)
        val radius = Coordinates(2).radius(coordinate)
        val expected = listOf(
            Coordinate(1, 1),
            Coordinate(1, 2),
            Coordinate(2, 1),
        )

        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, expected.size)
    }

    @Test
    fun radius_of_one_tile_board() {
        val coordinate = Coordinate(1, 1)
        val radius = Coordinates(1).radius(coordinate)
        val expected = emptyList<Coordinate>()

        assertTrue(radius.containsAll(expected))
        assertEquals(radius.size, 0)
    }

    @Test
    fun radius_of_invalid_big_coordinate() {
            val thrown: IllegalArgumentException = assertThrows(
                IllegalArgumentException::class.java,

                ) {

                val coordinate = Coordinate(100, 100)
                Coordinates(10).radius(coordinate)
            }

            assertTrue(thrown.message == "Invalid coordinate")
    }

    @Test
    fun move_coordinate_to_selected_place(){
        val coordinate = Coordinate(1, 1)
        val moved = Coordinates(10).move(coordinate,4,4)
        val expected = Coordinate(5,5)

        assertEquals(moved,expected)
    }

    @Test
    fun move_coordinate_to_selected_invalid_place(){
        val thrown: Exception = assertThrows(
            Exception::class.java,

            ) { val coordinate = Coordinate(1, 1)
            Coordinates(10).move(coordinate,11,4)
        }
        assertTrue(thrown.message == "Unable to move vertically")
    }

}
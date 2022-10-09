package pt.isel.daw.dawbattleshipgame.domain.board

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class CoordinatesTest {

    @Test
    fun moveFromTo_test_with_right_values() {
        val testCoordinates : CoordinateSet = setOf(
            Coordinate(1,1),
            Coordinate(1,2),
            Coordinate(1,3),
            Coordinate(1,4),
        )
        val origin = Coordinate(1,2)
        val destination = Coordinate(2,2)

        val newCoordinates = testCoordinates.moveFromTo(origin, destination, 10)
        requireNotNull(newCoordinates)

        val expectedCoordinates : CoordinateSet = setOf(
            Coordinate(2,1),
            Coordinate(2,2),
            Coordinate(2,3),
            Coordinate(2,4),
        )

        assertTrue(newCoordinates.containsAll(expectedCoordinates))
        assertEquals(newCoordinates.size, expectedCoordinates.size)

    }

}
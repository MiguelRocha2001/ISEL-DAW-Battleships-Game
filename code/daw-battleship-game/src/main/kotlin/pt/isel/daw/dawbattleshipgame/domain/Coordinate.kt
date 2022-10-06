package pt.isel.daw.dawbattleshipgame.domain

const val ONE = 1

/**
 * Converts, for example, "f4" to Coordinates(row: 4, column: 6).
 */
fun String.toCoordinate(): Coordinate? {
    return when (this.length) {
        2 -> {
            val column = this[0].lowercase()[0] - 'a' + 1
            val row = this[1] - '0'
            return Coordinate(row, column)
        }
        3 -> {
            val column = this[0].lowercase()[0] - 'a' + 1
            val row = this[2] - '0' + (this[1] - '0') * 10
            Coordinate(row, column)
        }
        else -> null
    }
}
typealias CoordinateSet = Set<Coordinate>

fun CoordinateSet.rotate(orientation: Orientation, origin: Coordinate) = this.map { it.rotate(orientation, origin) }.toSet()
private fun CoordinateSet.sorted() = sortedBy { it.row }.sortedBy { it.column }

/** Obtains the first coordinate (with lower row and column) */
fun CoordinateSet.first() = sorted().first()

/** Obtains the index of the coordinate, for implicit CoordinateSet */
fun CoordinateSet.index(c: Coordinate): Int? {
    if (all { c != it }) return null
    return sorted().indexOf(c)
}

/**
 * Represents a set of coordinates.
 * @param dim ensures the creation of all valid coordinates
 */
class Coordinates(private val dim: Int) {
    private fun Int.isOne() = this == ONE

    private fun Int.isGameDim() = this == dim

    fun random() = Coordinate(
        (1..dim).random(),
        (1..dim).random(),
    )

    /**
     * Generates all possible/valid coordinates.
     */
    fun values() = (0 until dim * dim).map {
        Coordinate((it / dim + ONE), (it % dim) + ONE)
    }

    fun up(c: Coordinate) = if(c.row.isOne()) null
    else Coordinate(c.row - ONE, c.column)

    fun down(c: Coordinate) = if (c.row.isGameDim()) null
    else Coordinate(c.row + ONE, c.column)

    fun left(c: Coordinate) = if (c.column.isOne()) null
    else Coordinate(c.row, c.column - ONE)

    fun right(c: Coordinate) = if (c.column.isGameDim()) null
    else Coordinate(c.row, c.column + ONE)

    /**
     * @return a not null list of coordinates corresponding to the coordinates adjacent to the instance
     * example:
     * [] [] []
     * [] {} []
     * [] [] []
     * being '{}' as the instance coordinate
     * amd '[]' the adjacent coordinates
     */
    fun radius(c: Coordinate): List<Coordinate> {
        val left = left(c)
        val right = right(c)
        return listOfNotNull(
            up(c), down(c), left, right,
            right?.let { up(it) },
            left?.let { up(it) },
            right?.let { down(it) },
            left?.let { down(it) },
        )
    }

    fun getDimension() = dim
}

class Coordinate(val row: Int, val column: Int) {
    override fun equals(other: Any?): Boolean {
        return if (other is Coordinate) {
            return row == other.row && column == other.column
        }
        else false
    }

    override fun toString() = "(column = ${('A' + column) - ONE }, row = $row)"

    fun rotate(orientation: Orientation, origin: Coordinate) =
        if(orientation.isVertical()) Coordinate(row + (column - origin.column), origin.column)
        else Coordinate(origin.row,column + (row - origin.row))

    override fun hashCode(): Int {
        var result = row
        result = 31 * result + column
        return result
    }
}
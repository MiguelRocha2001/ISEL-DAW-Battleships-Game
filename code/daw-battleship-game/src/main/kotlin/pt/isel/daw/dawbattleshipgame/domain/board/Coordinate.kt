package pt.isel.daw.dawbattleshipgame.domain.board

import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation


const val ONE = 1

/**
 * Converts, for example, "f4" to Coordinates(row: 4, column: 6).
 */
fun String.toCoordinateOrNull(): Coordinate? {
    val regex = Regex("^[a-zA-Z]\\d\\d?\$")
    if(!regex.matches(this)) return null

    val row = Regex("\\d\\d?")
        .find(this)?.value ?: return null

    val column = this.first().lowercaseChar() - 'a' + ONE

    return Coordinate(row.toInt(), column)
}

/**
 * Converts, for example, "f4" to Coordinates(row: 4, column: 6).
 * @throws IllegalArgumentException if [this] is of wrong format
 */
fun String.toCoordinate(): Coordinate {
    val regex = Regex("^[a-zA-Z]\\d\\d?\$")
    if(!regex.matches(this))
        throw IllegalArgumentException()

    val row = Regex("\\d\\d?")
        .find(this)?.value ?: throw IllegalStateException()

    val column = this.first().lowercaseChar() - 'a' + ONE

    return Coordinate(row.toInt(), column)
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

fun CoordinateSet.moveFromTo(origin : Coordinate, destination: Coordinate, gameDim : Int): CoordinateSet {
    val operator = Coordinates(gameDim)
    val horizontalAmount = destination.row - origin.row
    val verticalAmount = destination.column - origin.column

    val newCoordinates = this.map {
        operator.move(it, horizontalAmount, verticalAmount)
    }.toSet()

    return newCoordinates
}
/**
 * Represents a set of coordinates.
 * @param dim ensures the creation of all valid coordinates
 */
class Coordinates(private val dim: Int) {
    /**
     * Check if value equals one (1)
     */
    private fun Int.isOne() = this == ONE

    /**
     * Check if value equals game dimension
     */
    private fun Int.isGameDim() = this == dim

    /**
     * Generates a random valid Coordinate
     */
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

    fun move(c : Coordinate, verticalAmount : Int, horizontalAmount : Int): Coordinate {
        val aux = moveHorizontally(c, horizontalAmount)
        return moveVertically(aux, verticalAmount)
    }

    private fun moveVertically(c: Coordinate, amount: Int): Coordinate {
        if (c.row.isOne() && amount < 0) throw Exception("Unable to move vertically")
        if (c.row.isGameDim() && amount > 0) throw Exception("Unable to move vertically")
        return Coordinate(c.row + amount, c.column)
    }


    private fun moveHorizontally(c: Coordinate, amount : Int): Coordinate {
        if (c.column.isOne() && amount < 0) throw Exception("Unable to move horizontally")
        if (c.column.isGameDim() && amount > 0) throw Exception("Unable to move horizontally")
        return Coordinate(c.row, c.column + amount)
    }


    /**
     * Moves coordinate up, x "amount" of times, by default is ONE (1)
     */
    fun up(c: Coordinate, amount : Int = ONE) = if(c.row.isOne()) null
    else Coordinate(c.row - amount, c.column)

    /**
     * Moves coordinates down, x "amount" of times, by default is ONE (1)
     */
    fun down(c: Coordinate, amount : Int = ONE) = if (c.row.isGameDim()) null
    else Coordinate(c.row + amount, c.column)

    /**
     * Moves coordinates left, x "amount" of times, by default is ONE (1)
     */
    fun left(c: Coordinate, amount : Int = ONE) = if (c.column.isOne()) null
    else Coordinate(c.row, c.column - amount)

    /**
     * Moves coordinates right, x "amount" of times, by default is ONE (1)
     */
    fun right(c: Coordinate, amount : Int = ONE) = if (c.column.isGameDim()) null
    else Coordinate(c.row, c.column + amount)

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
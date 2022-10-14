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
        throw IllegalArgumentException("Invalid coordinate string format")

    val row = Regex("\\d\\d?")
        .find(this)?.value ?: throw IllegalStateException()

    val column = this.first().lowercaseChar() - 'a' + ONE

    return Coordinate(row.toInt(), column)
}

typealias CoordinateSet = Set<Coordinate>

private fun CoordinateSet.sorted() = sortedBy { it.row }.sortedBy { it.column }

/** Obtains the first coordinate (with lower row and column) */
fun CoordinateSet.first() = sorted().first()

/**
 * Moves all coordinates to a new position calculated using the [destination]
 */
fun CoordinateSet.moveFromTo(origin : Coordinate, destination: Coordinate, gameDim : Int): CoordinateSet {
    if(!this.contains(origin)) throw Exception("The origin coordinate is not included in the set of coordinates") //provisional
    val operator = Coordinates(gameDim)
    val horizontalAmount = destination.column - origin.column
    val verticalAmount = destination.row - origin.row

    val newCoordinates = this.map {
        operator.move(it,verticalAmount, horizontalAmount)
    }.toSet()

    return newCoordinates
}
/**
 * Represents a set of coordinates.
 * @param dim number of the tiles on the side of the board, ensures the creation of all valid coordinates
 */
class Coordinates(private val dim: Int) {

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

    /**
     * @param c the original coordinate
     * @param verticalAmount the amount of times to move the column
     * @param horizontalAmount the amount of times to move the row
     * Moves a coordinate to a new place (row , column) given an amount of moves to make
     * @returns a new Coordinate with the moved row and column
     */
    fun move(c : Coordinate, verticalAmount : Int, horizontalAmount : Int): Coordinate {
        val aux = moveHorizontally(c, horizontalAmount)
        return moveVertically(aux, verticalAmount)
    }

    /**
     * Moves vertically a coordinate, up or down depending on the amount of times
     * if [amount] is a positive number, moves up on the column i.e A -> B
     * if [amount] is a negative number, moves down on the column i.e B -> A
     * @return a new [Coordinate] with the column affected
     */
    private fun moveVertically(c: Coordinate, amount: Int): Coordinate {
        if (c.row + amount <= 0  && amount < 0) throw Exception("Unable to move vertically")
        if (c.row + amount > dim && amount > 0) throw Exception("Unable to move vertically")
        return Coordinate(c.row + amount, c.column)
    }


    /**
     * Moves horizontally a coordinate, right or left depending on the amount of times
     * if [amount] is a positive number, moves right on the row i.e 1 -> 2
     * if [amount] is a negative number, moves left on the row i.e 2 -> 1
     * @return a new [Coordinate] with the row affected
     */
    private fun moveHorizontally(c: Coordinate, amount : Int): Coordinate {
        if (c.column + amount <= 0  && amount < 0) throw Exception("Unable to move horizontally")
        if (c.column + amount > dim && amount > 0) throw Exception("Unable to move horizontally")
        return Coordinate(c.row, c.column + amount)
    }


    /**
     * Moves coordinate up, x "amount" of times, by default is ONE (1)
     */
    fun up(c: Coordinate, amount : Int = ONE) = if(c.row - amount <= 0 || amount < 0) null
    else Coordinate(c.row - amount, c.column)

    /**
     * Moves coordinates down, x "amount" of times, by default is ONE (1)
     */
    fun down(c: Coordinate, amount : Int = ONE) = if (c.row + amount > dim || amount < 0) null
    else Coordinate(c.row + amount, c.column)

    /**
     * Moves coordinates left, x "amount" of times, by default is ONE (1)
     */
    fun left(c: Coordinate, amount : Int = ONE) = if (c.column - amount <= 0 || amount < 0) null
    else Coordinate(c.row, c.column - amount)

    /**
     * Moves coordinates right, x "amount" of times, by default is ONE (1)
     */
    fun right(c: Coordinate, amount : Int = ONE) = if (c.column + amount > dim || amount < 0) null
    else Coordinate(c.row, c.column + amount)

    /**
     * @return a not null list of coordinates corresponding to the coordinates adjacent to the instance if [c] is a valid coordinate
     * example:
     * [] [] []
     * [] {} []
     * [] [] []
     * being '{}' as the instance coordinate
     * amd '[]' the adjacent coordinates
     */
    fun radius(c: Coordinate): List<Coordinate> {
        c.checkValid(dim)
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
    init {
        check(row > 0 && column > 0) {
            "Row or Column cannot be lower than 1"
        }
    }
    override fun equals(other: Any?): Boolean {
        return if (other is Coordinate) {
            return row == other.row && column == other.column
        }
        else false
    }

    fun checkValid(dimension : Int) {
        if(!(this.column <= dimension && this.row <= dimension)){
            throw IllegalArgumentException("Invalid coordinate")
        }
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
package pt.isel.daw.dawbattleshipgame.domain.board

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType


class Panel(
    val coordinate: Coordinate,
    val shipType: ShipType? = null,
    val isHit: Boolean = false,
) {
    /**
     * Gets panel database representation conforming to this table:
     * |----------------------------------|
     * |-TYPE-     -IS_NOT_HIT-   -IS_HIT-|
     * | null           ' '         'x'   |
     * | CARRIER        'C'         'c'   |
     * | BATTLESHIP     'B'         'b'   |
     * | CRUISER        'K'         'k'   |
     * | SUBMARINE      'S'         's'   |
     * | DESTROYER      'D'         'd'   |
     * |----------------------------------|
     */
    fun getDbIcon(): Char {
        return shipType?.getIcon(isHit) ?: if (isHit) HIT else WATER
    }

    companion object {
        const val HIT = 'x'
        const val WATER = ' '
    }

    fun hit() = if (!isHit) Panel(coordinate, shipType, true) else this

    fun isShip() = shipType != null

    override fun toString(): String {
        return if (isShip()) if (isHit) "X" else "[]"
        else if (isHit) "x" else "  "
    }

}

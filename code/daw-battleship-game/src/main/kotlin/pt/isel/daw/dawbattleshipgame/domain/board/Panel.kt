package pt.isel.daw.dawbattleshipgame.domain.board

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class Panel(
    val coordinate: Coordinate,
    val shipType: ShipType? = null,
    val isHit: Boolean = false,
){
    fun hit() = if(!isHit) Panel(coordinate, shipType, true) else this

    fun isShip() = shipType != null

    override fun toString(): String {
        return if (isShip()) if (isHit) "X" else "[]"
        else if (isHit) "x" else "  "
    }

    fun getType() = when(shipType){
            ShipType.CRUISER -> "cruiser"
            ShipType.CARRIER -> "carrier"
            ShipType.BATTLESHIP -> "battleship"
            ShipType.SUBMARINE -> "submarine"
            ShipType.DESTROYER -> "destroyer"
            else -> "water"
        }

}

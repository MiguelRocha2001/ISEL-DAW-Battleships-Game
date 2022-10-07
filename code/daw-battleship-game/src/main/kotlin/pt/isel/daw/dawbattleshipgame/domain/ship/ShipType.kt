package pt.isel.daw.dawbattleshipgame.domain.ship

import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.types.*

enum class ShipType(val length: Int) {
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    SUBMARINE(3),
    DESTROYER(2)
}

fun ShipType.generateShip(
    coordinateSet: CoordinateSet,
    orientation: Orientation = Orientation.HORIZONTAL,
    length: Int? = null,
) : Ship{
    val l = length ?: this.length
    return when(this){
        ShipType.CARRIER -> Carrier(orientation, coordinateSet, l)
        ShipType.BATTLESHIP -> Battleship(orientation, coordinateSet, l)
        ShipType.CRUISER -> Cruiser(orientation, coordinateSet, l)
        ShipType.SUBMARINE -> Submarine(orientation, coordinateSet, l)
        ShipType.DESTROYER -> Destroyer(orientation, coordinateSet, l)
    }
}
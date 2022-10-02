package pt.isel.daw.dawbattleshipgame.model.ship

import pt.isel.daw.dawbattleshipgame.model.CoordinateSet
import pt.isel.daw.dawbattleshipgame.model.Orientation
import pt.isel.daw.dawbattleshipgame.model.ship.types.*

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
) : Ship{
    return when(this){
        ShipType.CARRIER -> Carrier(orientation, coordinateSet)
        ShipType.BATTLESHIP -> Battleship(orientation, coordinateSet)
        ShipType.CRUISER -> Cruiser(orientation, coordinateSet)
        ShipType.SUBMARINE -> Submarine(orientation, coordinateSet)
        ShipType.DESTROYER -> Destroyer(orientation, coordinateSet)
    }
}
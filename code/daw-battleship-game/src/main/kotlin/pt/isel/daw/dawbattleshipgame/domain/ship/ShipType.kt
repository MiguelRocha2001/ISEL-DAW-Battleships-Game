package pt.isel.daw.dawbattleshipgame.domain.ship

import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.types.*

enum class ShipType {
    CARRIER,
    BATTLESHIP,
    CRUISER,
    SUBMARINE,
    DESTROYER
}

fun ShipType.generateShip(coordinateSet: CoordinateSet) = when (this) {
        ShipType.CARRIER -> Carrier(coordinateSet)
        ShipType.BATTLESHIP -> Battleship(coordinateSet)
        ShipType.CRUISER -> Cruiser(coordinateSet)
        ShipType.SUBMARINE -> Submarine(coordinateSet)
        ShipType.DESTROYER -> Destroyer(coordinateSet)
}
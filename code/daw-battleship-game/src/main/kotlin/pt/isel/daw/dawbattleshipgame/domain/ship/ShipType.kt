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

fun String.toShipType(): ShipType {
    return when (this.lowercase()) {
        "carrier" -> ShipType.CARRIER
        "battleship" -> ShipType.BATTLESHIP
        "cruiser" -> ShipType.CRUISER
        "submarine" -> ShipType.SUBMARINE
        "destroyer" -> ShipType.DESTROYER
        else -> throw IllegalArgumentException("Invalid ship type")
    }
}

fun ShipType.generateShip(coordinateSet: CoordinateSet) = when (this) {
        ShipType.CARRIER -> Carrier(coordinateSet)
        ShipType.BATTLESHIP -> Battleship(coordinateSet)
        ShipType.CRUISER -> Cruiser(coordinateSet)
        ShipType.SUBMARINE -> Submarine(coordinateSet)
        ShipType.DESTROYER -> Destroyer(coordinateSet)
}
package pt.isel.daw.dawbattleshipgame.domain.ship

import pt.isel.daw.dawbattleshipgame.domain.Coordinate

typealias ShipSet = Set<Ship>

fun ShipSet.getShipByType(shipType: ShipType) = this.firstOrNull { it.type == shipType }

fun ShipSet.getShip(position: Coordinate) =
    this.firstOrNull { it.coordinates.any { c -> c == position}}

fun ShipSet.addOrReplaceShip(ship : Ship): ShipSet {
    val auxMutableSet = this.toMutableSet()
    val entry = auxMutableSet.getShipByType(ship.type)
    if(entry != null) auxMutableSet.remove(entry)
    auxMutableSet.add(ship)
    return auxMutableSet
}

fun ShipSet.hasShip(shipType: ShipType) = this.any { it.type == shipType }

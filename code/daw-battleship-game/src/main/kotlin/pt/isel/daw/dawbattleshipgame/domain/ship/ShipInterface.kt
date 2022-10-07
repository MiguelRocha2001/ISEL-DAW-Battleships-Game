package pt.isel.daw.dawbattleshipgame.domain.ship

import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet

interface ShipInterface{
    val length: Int
    val orientation: Orientation
    val coordinates : CoordinateSet
    fun updateCoordinates(coordinates: CoordinateSet) : ShipInterface
    fun rotateCoordinates() : ShipInterface
}


abstract class Ship(val type : ShipType, val name : String) : ShipInterface

package pt.isel.daw.dawbattleshipgame.model.ship

import pt.isel.daw.dawbattleshipgame.model.CoordinateSet
import pt.isel.daw.dawbattleshipgame.model.Orientation

interface ShipInterface{
    val length: Int
    val orientation: Orientation
    val coordinates : CoordinateSet
    fun updateCoordinates(coordinates: CoordinateSet) : ShipInterface
    fun rotateCoordinates() : ShipInterface
}


abstract class Ship(val type : ShipType, val name : String) : ShipInterface

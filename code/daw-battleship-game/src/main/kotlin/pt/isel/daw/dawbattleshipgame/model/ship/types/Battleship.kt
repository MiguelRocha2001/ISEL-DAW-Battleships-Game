package pt.isel.daw.dawbattleshipgame.model.ship.types

import pt.isel.daw.dawbattleshipgame.model.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.model.ship.ShipType
import pt.isel.daw.dawbattleshipgame.model.CoordinateSet
import pt.isel.daw.dawbattleshipgame.model.Orientation
import pt.isel.daw.dawbattleshipgame.model.first
import pt.isel.daw.dawbattleshipgame.model.rotate
import pt.isel.daw.dawbattleshipgame.model.ship.Ship

class Battleship(
    override val orientation: Orientation,
    override val coordinates: CoordinateSet,
    override val length: Int = 4
): Ship(ShipType.BATTLESHIP, "Battleship") {
    override fun updateCoordinates(coordinates: CoordinateSet) = Battleship(orientation, coordinates, length)
    override fun rotateCoordinates(): ShipInterface = Battleship(
        orientation.other(),
        coordinates.rotate(orientation.other(), coordinates.first()),
        length
    )
}
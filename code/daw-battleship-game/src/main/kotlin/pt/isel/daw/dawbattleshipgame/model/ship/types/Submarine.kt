package pt.isel.daw.dawbattleshipgame.model.ship.types

import pt.isel.daw.dawbattleshipgame.model.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.model.ship.ShipType
import pt.isel.daw.dawbattleshipgame.model.CoordinateSet
import pt.isel.daw.dawbattleshipgame.model.Orientation
import pt.isel.daw.dawbattleshipgame.model.first
import pt.isel.daw.dawbattleshipgame.model.rotate
import pt.isel.daw.dawbattleshipgame.model.ship.Ship

class Submarine(
    override val orientation: Orientation,
    override val coordinates: CoordinateSet,
    override val length: Int = 3,
): Ship(ShipType.SUBMARINE, "Submarine") {
    override fun updateCoordinates(coordinates: CoordinateSet) = Submarine(orientation, coordinates, length)
    override fun rotateCoordinates(): ShipInterface = Submarine(
        orientation.other(),
        coordinates.rotate(orientation.other(), coordinates.first()),
        length
    )
}

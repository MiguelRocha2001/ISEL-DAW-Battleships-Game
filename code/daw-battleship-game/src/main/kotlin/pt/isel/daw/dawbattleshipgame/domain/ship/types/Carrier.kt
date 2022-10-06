package pt.isel.daw.dawbattleshipgame.domain.ship.types

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.Orientation
import pt.isel.daw.dawbattleshipgame.domain.first
import pt.isel.daw.dawbattleshipgame.domain.rotate
import pt.isel.daw.dawbattleshipgame.domain.ship.Ship

class Carrier(
    override val orientation: Orientation,
    override val coordinates: CoordinateSet,
    override val length: Int = 5,
    ) : Ship(ShipType.CARRIER, "Carrier") {
    override fun updateCoordinates(coordinates: CoordinateSet) = Carrier(orientation, coordinates, length)
    override fun rotateCoordinates(): ShipInterface = Carrier(
        orientation.other(),
        coordinates.rotate(orientation.other(), coordinates.first()),
        length
    )
}

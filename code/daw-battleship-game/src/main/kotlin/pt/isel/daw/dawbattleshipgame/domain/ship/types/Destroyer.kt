package pt.isel.daw.dawbattleshipgame.domain.ship.types

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.Orientation
import pt.isel.daw.dawbattleshipgame.domain.first
import pt.isel.daw.dawbattleshipgame.domain.rotate
import pt.isel.daw.dawbattleshipgame.domain.ship.Ship

class Destroyer(
    override val orientation: Orientation,
    override val coordinates: CoordinateSet,
    override val length: Int = 2,
): Ship(ShipType.DESTROYER, "Destroyer") {
    override fun updateCoordinates(coordinates: CoordinateSet) = Destroyer(orientation, coordinates, length)
    override fun rotateCoordinates(): ShipInterface = Destroyer(
        orientation.other(),
        coordinates.rotate(orientation.other(), coordinates.first()),
        length
    )
}
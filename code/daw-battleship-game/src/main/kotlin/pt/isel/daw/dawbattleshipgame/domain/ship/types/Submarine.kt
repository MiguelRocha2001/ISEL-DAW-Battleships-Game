package pt.isel.daw.dawbattleshipgame.domain.ship.types

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.board.first
import pt.isel.daw.dawbattleshipgame.domain.board.rotate
import pt.isel.daw.dawbattleshipgame.domain.ship.Ship

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

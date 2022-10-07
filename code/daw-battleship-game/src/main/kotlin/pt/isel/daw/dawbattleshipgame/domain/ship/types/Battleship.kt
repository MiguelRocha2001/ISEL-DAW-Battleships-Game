package pt.isel.daw.dawbattleshipgame.domain.ship.types

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipInterface
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.board.first
import pt.isel.daw.dawbattleshipgame.domain.board.rotate
import pt.isel.daw.dawbattleshipgame.domain.ship.Ship

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
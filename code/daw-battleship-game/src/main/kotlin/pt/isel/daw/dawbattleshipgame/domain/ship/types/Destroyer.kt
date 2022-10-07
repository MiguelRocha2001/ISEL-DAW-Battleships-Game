package pt.isel.daw.dawbattleshipgame.domain.ship.types

import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class Destroyer(override val coordinates: CoordinateSet): Ship() {
    override val type: ShipType = ShipType.DESTROYER
}
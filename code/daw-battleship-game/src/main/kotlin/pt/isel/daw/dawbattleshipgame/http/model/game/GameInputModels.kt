package pt.isel.daw.dawbattleshipgame.http.model.game

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration

data class CreateGameInputModel(val configuration: Configuration)

data class PlaceShipInputModel(
    val shipType: ShipType,
    val position: Coordinate,
    val orientation: Orientation
)

data class MoveShipInputModel(
    val origin: Coordinate,
    val destination: Coordinate
)
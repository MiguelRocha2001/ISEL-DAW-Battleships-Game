package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

data class CreateGameInputModel(
    val boardSize: Int,
    val fleet: Map<ShipTypeInputModel, Int>,
    val nShotsPerRound: Int,
    val roundTimeout: Int
)

data class PlaceShipInputModel(
    val shipType: ShipTypeInputModel,
    val position: CoordinateInputModel,
    val orientation: OrientationInputModel
)

data class CoordinateInputModel(val row: Int, val column: Int) {
    init {
        require(row > 0) { "Row must be greater than 0" }
        require(column > 0) { "Column must be greater than 0" }
    }
}
enum class OrientationInputModel {
    HORIZONTAL,
    VERTICAL;

    @JsonValue
    fun getName() = name
}

fun OrientationInputModel.toOrientation(): Orientation = when (this) {
    OrientationInputModel.HORIZONTAL -> Orientation.HORIZONTAL
    OrientationInputModel.VERTICAL -> Orientation.VERTICAL
}
enum class ShipTypeInputModel {
    CARRIER,
    BATTLESHIP,
    CRUISER,
    SUBMARINE,
    DESTROYER;

    @JsonValue
    fun getName() = name
}

fun ShipTypeInputModel.toShipType() = when (this) {
    ShipTypeInputModel.CARRIER -> ShipType.CARRIER
    ShipTypeInputModel.BATTLESHIP -> ShipType.BATTLESHIP
    ShipTypeInputModel.CRUISER -> ShipType.KRUISER
    ShipTypeInputModel.SUBMARINE -> ShipType.SUBMARINE
    ShipTypeInputModel.DESTROYER -> ShipType.DESTROYER
}

data class MoveShipInputModel(
    val origin: Coordinate,
    val destination: Coordinate
)
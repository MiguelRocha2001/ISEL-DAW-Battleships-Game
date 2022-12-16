package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

data class CreateGameInputModel(
        val boardSize: Int,
        val fleet: Map<ShipTypeInputModel, Int>,
        val nShotsPerRound: Int,
        val roundTimeout: Long
) {
    init {
        require(boardSize in 8..15) {
            "Board size must be in range [8..15]"
        }
        require(fleet.isNotEmpty()) {
            "There must be at least one boat"
        }
    }
}

data class CoordinateInputModel(val row: Int, val column: Int) {
    fun toCoordinate() = Coordinate(row, column)

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
    ShipTypeInputModel.CRUISER -> ShipType.CRUISER
    ShipTypeInputModel.SUBMARINE -> ShipType.SUBMARINE
    ShipTypeInputModel.DESTROYER -> ShipType.DESTROYER
}

data class FleetStateInputModel(val fleetConfirmed: Boolean)

// @JsonDeserialize(`as` = PlaceShipsInputModel::class)
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "operation")
@JsonSubTypes(
    JsonSubTypes.Type(value = PlaceShipsInputModel::class, name = "place-ships"),
    JsonSubTypes.Type(value = AlterShipInputModel::class, name = "alter-ship"),
)
sealed class PostShipsInputModel(open val operation: String)

data class PlaceShipsInputModel(
    val ships: List<PlaceShipInputModel>,
    val fleetConfirmed: Boolean
): PostShipsInputModel("place-ships")
data class PlaceShipInputModel(
    val shipType: ShipTypeInputModel,
    val position: CoordinateInputModel,
    val orientation: OrientationInputModel
)

class AlterShipInputModel(
    val origin: CoordinateInputModel,
    val destination: CoordinateInputModel?, // null if ship is to be rotated
): PostShipsInputModel("alter-ship")
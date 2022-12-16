package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_ARGUMENT
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_INPUT
import pt.isel.daw.dawbattleshipgame.http.requireWithException

data class CreateGameInputModel(
        val boardSize: Int,
        val fleet: Map<ShipTypeInputModel, Int>,
        val shots: Int,
        val roundTimeout: Long
) {
    init {
        requireWithException(
                INVALID_INPUT,
                "Board size must be in range [8..15]") {
            boardSize in 8..15
        }
        requireWithException(INVALID_INPUT,
                "There must be at least one boat") {
            fleet.isNotEmpty()
        }
        requireWithException(INVALID_INPUT,
                "Number of shots per round must be in range [1..5]"){
            shots in (1..5)
        }
        requireWithException(INVALID_INPUT,
                "Round timeout must be in range [10..240]"){
            roundTimeout in 10..240
        }

    }
}

data class CoordinateInputModel(val row: Int, val column: Int) {
    fun toCoordinate() = Coordinate(row, column)
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
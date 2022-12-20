package pt.isel.daw.dawbattleshipgame.domain.game

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_ARGUMENT
import pt.isel.daw.dawbattleshipgame.http.requireWithException
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer.ValueParametersHandler.DEFAULT

/**
 * Represents the game configuration
 */
data class Configuration(
        @JsonProperty("boardSize")
    val boardSize: Int,
        @JsonProperty("fleet")
    val fleet: Map<ShipType, Int>, // List<Ship:Occupation>
        @JsonProperty("shots")
    val shots: Long,
        @JsonProperty("roundTimeout")
    val roundTimeout: Long
) {
    init {
        requireWithException(
                INVALID_ARGUMENT,
                "Board size must be in range [8..15]") {
            boardSize in 8..15
        }
        requireWithException(INVALID_ARGUMENT,
                "There must be at least one boat") {
            fleet.isNotEmpty()
        }
        requireWithException(INVALID_ARGUMENT,
                "Number of shots per round must be in range [1..5]") {
            shots in (1..5)
        }
        requireWithException(INVALID_ARGUMENT,
                "Round timeout must be in range [10..240]") {
            roundTimeout in 10..240
        }

    }
    companion object {
        val mapper = ObjectMapper()

        val DEFAULT = Configuration(
                boardSize = 10,
                shots = 1,
                fleet = mapOf(
                        ShipType.CARRIER to 5,
                        ShipType.BATTLESHIP to 4,
                        ShipType.CRUISER to 3,
                        ShipType.SUBMARINE to 3,
                        ShipType.DESTROYER to 2,
                ),
                roundTimeout = 120
        )
    }

    fun isShipValid(shipType: ShipType) =
            fleet[shipType] != null

    fun isEqual(other: Configuration) =
            this.fleet == other.fleet &&
                    this.boardSize == other.boardSize &&
                    this.shots == other.shots &&
                    this.roundTimeout == other.roundTimeout
}
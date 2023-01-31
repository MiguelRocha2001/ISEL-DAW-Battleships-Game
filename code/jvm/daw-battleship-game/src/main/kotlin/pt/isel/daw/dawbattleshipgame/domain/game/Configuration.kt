package pt.isel.daw.dawbattleshipgame.domain.game

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_ARGUMENT
import pt.isel.daw.dawbattleshipgame.http.requireWithException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.absoluteValue

/**
 * Represents the game configuration
 */
data class Configuration(
        @JsonProperty("boardSize")
    val boardSize: Int,
        @JsonProperty("fleet")
    val fleet: Map<ShipType, Int>,
        @JsonProperty("shots")
    val shots: Long,
        @JsonProperty("roundTimeout")
    val roundTimeout: Long
) {
    init {
        requireWithException(
                INVALID_ARGUMENT,
                "Board size must be in range [8..13]") {
            boardSize in 8..13
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
                fleet = hashMapOf(
                        ShipType.CARRIER to 5,
                        ShipType.BATTLESHIP to 4,
                        ShipType.CRUISER to 3,
                        ShipType.SUBMARINE to 3,
                        ShipType.DESTROYER to 2,
                ),
                roundTimeout = 10
        )
    }

    override fun hashCode(): Int {
        return Objects.hash(fleet, boardSize, shots, roundTimeout)
    }
    fun isShipValid(shipType: ShipType) =
            fleet[shipType] != null

    fun isEqual(other: Configuration) =
            this.fleet == other.fleet &&
                    this.boardSize == other.boardSize &&
                    this.shots == other.shots &&
                    this.roundTimeout == other.roundTimeout
}
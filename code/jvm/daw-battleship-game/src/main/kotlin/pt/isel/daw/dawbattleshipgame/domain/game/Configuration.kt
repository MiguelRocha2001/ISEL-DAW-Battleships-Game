package pt.isel.daw.dawbattleshipgame.domain.game

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_ARGUMENT
import pt.isel.daw.dawbattleshipgame.http.model.game.CreateGameInputModel
import pt.isel.daw.dawbattleshipgame.http.pipeline.LoggerInterceptor
import pt.isel.daw.dawbattleshipgame.http.requireWithException
import java.util.*
import kotlin.collections.HashMap

/**
 * Represents the game configuration
 */
data class Configuration(
    @JsonProperty("boardSize")
    val boardSize: Int,
    @JsonProperty("fleet")
    val fleet: Map<ShipType, Int>,
    @JsonProperty("shots")
    val nShotsPerRound: Long,
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
            nShotsPerRound in (1..5)
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
                nShotsPerRound = 1,
                fleet = mapOf(
                        ShipType.CARRIER to 5,
                        ShipType.BATTLESHIP to 4,
                        ShipType.CRUISER to 3,
                        ShipType.SUBMARINE to 3,
                        ShipType.DESTROYER to 2,
                ),
                roundTimeout = 10
        )
    }

    private fun hashFleet(fleet: Map<ShipType, Int>): Int {
        val sortedFleet = fleet.toList().sortedBy { (shipType, _) -> shipType }
        var result = 1
        for ((shipType, count) in sortedFleet) {
            result = 31 * result + shipType.name.hashCode()
            result = 31 * result + count
        }
        return result
    }

    override fun hashCode(): Int {
        //log the value of the hash
        val logger = LoggerFactory.getLogger(LoggerInterceptor::class.java)
        //cast map to hashMap for docker to work
        val a = Objects.hash(hashFleet(fleet),boardSize, nShotsPerRound, roundTimeout)
        logger.info("hash: $a")
        return a
    }
    fun isShipValid(shipType: ShipType) =
            fleet[shipType] != null

    fun isEqual(other: Configuration) =
            this.fleet == other.fleet &&
                    this.boardSize == other.boardSize &&
                    this.nShotsPerRound == other.nShotsPerRound &&
                    this.roundTimeout == other.roundTimeout


}


fun CreateGameInputModel.validate(): Boolean {
    //do the checks return true if everything is ok
    if(boardSize !in 8..13) return false
    if(fleet.isEmpty()) return false
    if(nshotsPerRound !in 1..5) return false
    if(roundTimeout !in 10..240) return false
    return true
}
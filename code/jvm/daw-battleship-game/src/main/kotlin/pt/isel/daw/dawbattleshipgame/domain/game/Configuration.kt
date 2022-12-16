package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.INVALID_ARGUMENT
import pt.isel.daw.dawbattleshipgame.http.requireWithException

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val shots: Int,
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
                "Number of shots per round must be in range [1..5]"){
            shots in (1..5)
        }
        requireWithException(INVALID_ARGUMENT,
                "Round timeout must be in range [10..240]"){
            roundTimeout in 10..240
        }

    }

    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null
}
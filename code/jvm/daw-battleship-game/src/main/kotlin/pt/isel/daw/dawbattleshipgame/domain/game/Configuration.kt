package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import java.time.Duration

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val nShotsPerRound: Int,
    val roundTimeout: Long
) {
    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null
}
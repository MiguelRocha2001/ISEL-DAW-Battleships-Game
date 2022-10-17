package pt.isel.daw.dawbattleshipgame.domain.state

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val nShotsPerRound: Int,
    val roundTimeout: Int
) {
    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null
}
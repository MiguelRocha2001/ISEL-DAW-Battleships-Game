package pt.isel.daw.dawbattleshipgame.model

import pt.isel.daw.dawbattleshipgame.model.ship.ShipType

/**
 * Represents the game configuration
 */
class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val nShotsPerRound: Int,
    val roundTimeout: Int
) {
    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null
}
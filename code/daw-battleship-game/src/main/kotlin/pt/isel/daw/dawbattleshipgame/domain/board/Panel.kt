package pt.isel.daw.dawbattleshipgame.domain.board

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

sealed class Panel(internal val isHit: Boolean) {
    abstract fun getPanelHit(): Panel
}

/**
 * Class representing a panel of water
 * @param isHit if true has been hit, else it has not
 */
class WaterPanel(isHit: Boolean = false) : Panel(isHit) {
    override fun getPanelHit() = WaterPanel(true)

    override fun toString(): String {
        return if (isHit) "x" else "  "
    }
}

/**
 * Class representing a panel with a Ship
 * @param shipType is the type of the ship
 * @param isHit if true the ship has been hit, else it has not
 */
class ShipPanel(val shipType: ShipType, isHit: Boolean = false) : Panel(isHit) {
    override fun getPanelHit() = ShipPanel(shipType, true)

    override fun toString(): String {
        return if (isHit) "X" else "[]"
    }
}
package pt.isel.daw.dawbattleshipgame.domain.board

import org.slf4j.event.SubstituteLoggingEvent
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.types.Ship

enum class PanelType { WaterPanel, ShipPane }

sealed class Panel(internal val isHit: Boolean) {
    abstract fun getPanelHit(): Panel
}

class WaterPanel(isHit: Boolean = false) : Panel(isHit) {
    override fun getPanelHit() = WaterPanel(true)

    override fun toString(): String {
        return if (isHit) "x" else "  "
    }
}
class ShipPanel(val shipType: ShipType, isHit: Boolean = false) : Panel(isHit) {
    override fun getPanelHit() = ShipPanel(shipType, true)

    override fun toString(): String {
        return if (isHit) "X" else "[]"
    }
}
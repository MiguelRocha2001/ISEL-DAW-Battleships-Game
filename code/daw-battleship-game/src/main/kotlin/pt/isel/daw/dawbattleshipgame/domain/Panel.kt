package pt.isel.daw.dawbattleshipgame.domain

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
class ShipPanel(isHit: Boolean = false) : Panel(isHit) {
    override fun getPanelHit() = ShipPanel(true)

    override fun toString(): String {
        return if (isHit) "X" else "[]"
    }
}
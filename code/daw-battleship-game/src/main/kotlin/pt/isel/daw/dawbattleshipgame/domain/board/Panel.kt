package pt.isel.daw.dawbattleshipgame.domain.board

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
sealed class ShipPanel(isHit: Boolean = false) : Panel(isHit) {
    override fun toString(): String {
        return if (isHit) "X" else "[]"
    }
}

class BattleshipPanel(isHit: Boolean = false): ShipPanel() {
    override fun getPanelHit() = BattleshipPanel(true)
}

class CarrierPanel(isHit: Boolean = false): ShipPanel() {
    override fun getPanelHit() = CarrierPanel(true)
}

class CruiserPanel(isHit: Boolean = false): ShipPanel() {
    override fun getPanelHit() = CruiserPanel(true)
}

class DestroyerPanel(isHit: Boolean = false): ShipPanel() {
    override fun getPanelHit() = DestroyerPanel(true)
}

class SubmarinePanel(isHit: Boolean = false): ShipPanel() {
    override fun getPanelHit() = SubmarinePanel(true)
}
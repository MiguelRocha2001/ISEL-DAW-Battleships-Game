package pt.isel.daw.dawbattleshipgame.services


import pt.isel.daw.dawbattleshipgame.data.GameConfigData
import pt.isel.daw.dawbattleshipgame.data.UserData
import pt.isel.daw.dawbattleshipgame.model.Configuration

class GameConfigurationServices(private val data: GameConfigData) {
    fun setConfiguration(configuration: Configuration) {
        data.setConfiguration(configuration)
    }
}
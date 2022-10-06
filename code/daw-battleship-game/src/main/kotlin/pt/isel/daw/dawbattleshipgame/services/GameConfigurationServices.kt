package pt.isel.daw.dawbattleshipgame.services


import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.data.DataBase
import pt.isel.daw.dawbattleshipgame.domain.Configuration

@Component
class GameConfigurationServices(private val dataBase: DataBase) {
    fun setConfiguration(configuration: Configuration) {
        dataBase.saveConfiguration(configuration)
    }
}
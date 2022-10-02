package pt.isel.daw.dawbattleshipgame.data

import pt.isel.daw.dawbattleshipgame.model.Configuration

class GameConfigData {
    val configuration: Configuration
    private val dataBase = DataBase()

    constructor(configuration: Configuration) {
        this.configuration = configuration
    }

    fun setConfiguration(configuration: Configuration) {
        dataBase.saveConfiguration(configuration)
    }
}
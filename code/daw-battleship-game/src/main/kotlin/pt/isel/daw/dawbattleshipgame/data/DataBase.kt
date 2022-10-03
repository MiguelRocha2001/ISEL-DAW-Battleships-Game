package pt.isel.daw.dawbattleshipgame.data

import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Configuration
import pt.isel.daw.dawbattleshipgame.model.game.Game
import pt.isel.daw.dawbattleshipgame.model.game.State

class DataBase {

    init {
        // TODO: Create connection
    }

    internal fun saveGame(game: Game) {
        saveState(game.state)
        saveBoard(game.myBoard)
    }

    private fun saveState(state: State) {
        TODO("Not yet implemented")
    }

    private fun saveBoard(board: Board) {
        TODO("Not yet implemented")
    }

    fun getOpponentBoard(): Board {
        TODO("Not yet implemented")
    }

    fun createUser(username: String, password: String) {
        TODO("Not yet implemented")
    }

    fun getGame(): Game? {
        TODO("Not yet implemented")
    }

    fun saveConfiguration(configuration: Configuration) {
        TODO("Not yet implemented")
    }
}
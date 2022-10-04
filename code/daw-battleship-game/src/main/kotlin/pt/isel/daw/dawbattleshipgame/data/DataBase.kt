package pt.isel.daw.dawbattleshipgame.data

import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Configuration
import pt.isel.daw.dawbattleshipgame.model.Player
import pt.isel.daw.dawbattleshipgame.model.game.Game
import pt.isel.daw.dawbattleshipgame.model.game.State

class DataBase {

    init {
        // TODO: Create connection
    }

    internal fun saveGame(player: String, game: Game) {
        saveState(game.state)
        saveBoard(game.myBoard)
    }

    fun getGame(): Game? {
        TODO("Not yet implemented")
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

    fun saveConfiguration(configuration: Configuration) {
        TODO("Not yet implemented")
    }

    fun getCurrentPlayer(): Player {
        TODO("Not yet implemented")
    }

    fun login(username: String, password: String): Boolean {
        TODO("Not yet implemented")
    }
}
package pt.isel.daw.dawbattleshipgame.data

import org.jdbi.v3.core.Jdbi
import org.springframework.boot.jdbc.DataSourceBuilder
import pt.isel.daw.dawbattleshipgame.domain.Board
import pt.isel.daw.dawbattleshipgame.domain.Configuration
import pt.isel.daw.dawbattleshipgame.domain.Player
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.State


class DataBase {
    private val jdbi: Jdbi

    init {
        val dbPassword = System.getenv("DB_POSTGRES_PASSWORD")
        val dataSource = DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/postgres")
            .username("postgres")
            .password(dbPassword)
            .build()
        jdbi = Jdbi.create(dataSource)
        // TODO
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
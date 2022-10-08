package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.boot.jdbc.DataSourceBuilder
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.game.Game


class JdbiGamesRepository {
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

    internal fun saveGame(game: Game) {
        jdbi
    }

    fun getGame(gameId: Int): Game? {
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
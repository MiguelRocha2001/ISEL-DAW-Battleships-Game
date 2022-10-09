package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.springframework.boot.jdbc.DataSourceBuilder
import pt.isel.daw.dawbattleshipgame.domain.game.*

sealed class DbGameResponse
data class DbPreparationPhase(val preparationPhase: PreparationPhase) : DbGameResponse()
data class DbWaitingPhase(val waitingPhase: WaitingPhase) : DbGameResponse()
data class DbPlayerPreparationPhase(val playerPreparationPhase: PlayerPreparationPhase) : DbGameResponse()
data class DbBattlePhase(val game: BattlePhase) : DbGameResponse()

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
        TODO("Not yet implemented")
    }

    internal fun savePreparationPhase(preparationPhase: PreparationPhase) {
        TODO("Not yet implemented")
    }

    internal fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase) {
        TODO("Not yet implemented")
    }

    internal fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase) {
        TODO("Not yet implemented")
    }

    internal fun getPreparationPhase(gameId: Int): PreparationPhase? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the game if both players have confirmed their fleets.
     */
    internal fun getWaitingPhase(gameId: Int): DbWaitingPhase? {
        TODO("Not yet implemented")
    }

    internal fun getPlayerPreparationPhase(token: String): DbPlayerPreparationPhase? {
        TODO("Not yet implemented")
    }

    internal fun getGame(): DbGameResponse? {
        TODO("Not yet implemented")
    }


    fun createUser(username: String, password: String) {
        TODO("Not yet implemented")
    }

    fun saveConfiguration(configuration: Configuration) {
        TODO("Not yet implemented")
    }

    fun login(username: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    fun getWaitingUser(configuration: Configuration): String? {
        TODO("Not yet implemented")
    }

    fun joinGameQueue(token: String, configuration: Configuration) {
        TODO("Not yet implemented")
    }

    fun removeUserFromQueue(userWating: String) {
        TODO("Not yet implemented")
    }

    fun removeGame(gameId: Int) {
        TODO("Not yet implemented")
    }
}
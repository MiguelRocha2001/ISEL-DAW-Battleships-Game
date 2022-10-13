package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.repository.jdbi.DbGameResponse
import pt.isel.daw.dawbattleshipgame.repository.jdbi.DbPlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.repository.jdbi.DbWaitingPhase

interface GamesRepository {
    fun saveGame(game: Game)

    fun savePreparationPhase(preparationPhase: PreparationPhase)

    fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase)
    fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase)
    fun getPreparationPhase(gameId: Int): PreparationPhase?

    fun getWaitingPhase(gameId: Int): DbWaitingPhase?
    fun getPlayerPreparationPhase(token: String): DbPlayerPreparationPhase?
    fun getGame(): DbGameResponse?
    fun createUser(username: String, password: String)
    fun saveConfiguration(configuration: Configuration)
    fun login(username: String, password: String): Boolean
    fun getWaitingUser(configuration: Configuration): String?
    fun joinGameQueue(token: String, configuration: Configuration)
    fun removeUserFromQueue(userWating: String)
    fun removeGame(gameId: Int)
    fun emptyDatabase()
}
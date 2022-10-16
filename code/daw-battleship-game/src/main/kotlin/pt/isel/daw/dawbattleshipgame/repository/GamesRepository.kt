package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerWaitingPhase

interface GamesRepository {
    fun saveGame(game: Game)

    fun savePreparationPhase(singlePhase: SinglePhase)

    fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase)
    fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase)
    fun getPreparationPhase(gameId: Int): SinglePhase?
    fun createUser(username: String, password: String)
    fun saveConfiguration(configuration: Configuration)
    fun login(username: String, password: String): Boolean
    fun joinGameQueue(userId: Int, configuration: Configuration)
    fun removeGame(gameId: Int)
    fun emptyRepository()
    fun getGame(gameId: Int): Game?
    fun getGameByUser(userId: Int): Game?
}
package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase



interface GamesRepository {
    fun createGame(configuration: Configuration, player1: Int, player2: Int) : Int?
    fun saveGame(game: Game)
    fun savePreparationPhase(singlePhase: SinglePhase)
    fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPhase)
    fun savePlayerWaitingPhase(playerWaitingPhase: PlayerPhase)

    fun getPreparationPhase(gameId: Int): SinglePhase?
    fun createUser(username: String, password: String)
    fun saveConfiguration(configuration: Configuration)
    fun login(username: String, password: String): Boolean
    fun removeGame(gameId: Int)
    fun emptyRepository()
    fun getGame(gameId: Int): Game?
    fun getGameByUser(userId: Int): Game?
    fun getGameIdByUser(userId : Int) : Int?
}
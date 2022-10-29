package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase


interface GamesRepository {
    fun saveGame(game: Game)
    fun savePreparationPhase(singlePhase: SinglePhase)
    fun removeGame(gameId: Int)
    fun emptyRepository()
    fun getGame(gameId: Int): Game?
    fun getGameByUser(userId: Int): Game?
    fun isInGame(userId: Int): Boolean
}
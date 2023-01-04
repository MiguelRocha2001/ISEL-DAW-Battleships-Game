package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.InitGame


interface GamesRepository {
    fun saveGame(game: Game)
    fun startGame(game: InitGame) : Int?
    fun removeGame(gameId: Int)
    fun emptyRepository()
    fun getAllGames(): List<Int>
    fun getGame(gameId: Int): Game?
    fun getGameByUser(userId: Int): Game?
    fun getNotFinishedGamesByUser(userId: Int): List<Int>
    fun isInGame(userId: Int): Boolean
    fun removeUserFromGame(userId: Int)
    fun updateGame(game: Game)
}
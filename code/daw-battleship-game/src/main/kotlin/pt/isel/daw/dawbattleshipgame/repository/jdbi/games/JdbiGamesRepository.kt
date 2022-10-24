package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository


class JdbiGamesRepository(
    private val handle: Handle,
): GamesRepository {
    override fun getGame(gameId: Int): Game? {
        return fetchGameInternal(handle, gameId)
    }

    override fun getGameByUser(userId: Int): Game? {
        return fetchGameByUser(handle, userId)
    }

    override fun saveGame(game: Game) {
        insertGame(handle, game)
        insertBoards(handle, game)
        insertConfiguration(handle, game.gameId, game.configuration)
    }

    override fun savePreparationPhase(singlePhase: SinglePhase) {
        saveGame(singlePhase)
    }

    override fun removeGame(gameId: Int) {
        deleteGame(handle, gameId)
    }

    override fun emptyRepository() {
        clearAllTables(handle)
    }
}
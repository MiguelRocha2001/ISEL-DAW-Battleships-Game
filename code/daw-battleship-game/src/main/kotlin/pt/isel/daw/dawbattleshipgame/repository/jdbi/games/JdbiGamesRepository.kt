package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
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

    override fun isInGame(userId: Int): Boolean {
        return handle.createQuery("select count(*) from game where player1 = :player1 or player2 = :player2")
            .bind("player1", userId)
            .bind("player2", userId)
            .mapTo<Int>()
            .single() != 0
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
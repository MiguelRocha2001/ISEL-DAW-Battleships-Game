package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.InitGame
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

    override fun getNotFinishedGamesByUser(userId: Int): List<Int> {
        return handle.createQuery(
            """
                SELECT id FROM GAME
                WHERE (player1 = :userId OR player2 = :userId) AND state != 'FINISHED'
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Int>()
            .list()
    }

    override fun isInGame(userId: Int): Boolean {
        return handle.createQuery("select count(*) from GAME where player1 = :player1 or player2 = :player2")
            .bind("player1", userId)
            .bind("player2", userId)
            .mapTo<Int>()
            .single() != 0
    }

    override fun saveGame(game: Game) {
        insertGame(handle, game)
        insertBoards(handle, game)
        insertConfiguration(handle, game.id, game.configuration)
    }

    override fun startGame(game : InitGame) : Int? {
        val gameId = makeGame(handle, game) ?: return null
        insertBoard(handle, gameId, game.player1, game.board1)
        insertBoard(handle, gameId, game.player2, game.board2)
        insertConfiguration(handle, gameId, game.configuration)
        return gameId
    }

    override fun removeGame(gameId: Int) {
        deleteGame(handle, gameId)
    }

    override fun emptyRepository() {
        clearAllTables(handle)
    }

    override fun getAllGames(): List<Int> {
        return handle.createQuery("select id from GAME")
            .mapTo<Int>()
            .list()
    }

    override fun removeUserFromGame(userId: Int) {
        handle.createUpdate("delete from USER_QUEUE where _user = :_user")
            .bind("_user", userId)
            .execute()
        val game = getGameByUser(userId) ?: return
        deleteGame(handle, game.id)
    }

    override fun updateGame(game: Game) {
        updateGame(handle, game)
        updateBoard(handle, game.board1, game.player1, game.id)
        updateBoard(handle, game.board2, game.player2, game.id)
    }
}
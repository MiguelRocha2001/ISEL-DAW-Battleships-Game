package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.Instants
import pt.isel.daw.dawbattleshipgame.domain.game.getDbState


internal fun fetchGameByUser(handle: Handle, userId: Int): Game? {
    val gameId = getGameIdByUser(handle, userId) ?: return null
    return fetchGameInternal(handle, gameId)
}

internal fun fetchGameInternal(handle: Handle, gameId: Int): Game? {
    val dbGameMapper = getDbGameMapper(handle, gameId) ?: return null
    val (player1DbBoardMapper, player2DbBoardMapper) = getDbBoardMappers(handle, gameId)

    val dbConfigurationMapper = getDbConfigurationMapper(handle, gameId) ?:
        throw IllegalStateException("Game $gameId has no configuration")

    val configuration = dbConfigurationMapper.toConfiguration()
    val player1Board = Board(player1DbBoardMapper.grid, player1DbBoardMapper.confirmed)
    val player2Board = Board(player2DbBoardMapper.grid, player2DbBoardMapper.confirmed)

    return Game(gameId, configuration,
        dbGameMapper.player1, dbGameMapper.player2,
        player1Board, player2Board,
        dbGameMapper.state.getDbState(),
        Instants.get(
                dbGameMapper.created,
                dbGameMapper.updated,
                dbGameMapper.deadline
        ),
        dbGameMapper.player_turn,
        dbGameMapper.winner
    )
}

private fun getGameIdByUser(handle: Handle, userId: Int): Int? {
    val dbGameMapper = handle.createQuery(
        """
            SELECT * FROM GAME
            WHERE player1 = :userId OR player2 = :userId
        """.trimIndent()
    )
        .bind("userId", userId)
        .mapTo<DbGameMapper>()
        .firstOrNull() ?: return null
    return dbGameMapper.id
}
private fun getDbGameMapper(handle: Handle, gameId: Int): DbGameMapper? {
    return handle.createQuery("select * from GAME where id = :id")
        .bind("id", gameId)
        .mapTo<DbGameMapper>()
        .singleOrNull()
}

private fun getDbBoardMappers(handle: Handle, gameId: Int): Pair<DbBoardMapper, DbBoardMapper> {
    val boards = handle.createQuery("select * from BOARD where game = :game")
        .bind("game", gameId)
        .mapTo<DbBoardMapper>()
        .toList()
    if (boards.size != 2) {
        throw IllegalStateException("Game $gameId has ${boards.size} boards")
    }
    return Pair(boards[0], boards[1])
}

private fun getDbConfigurationMapper(handle: Handle, gameId: Int): DbConfigurationMapper? {
    return handle.createQuery("select * from CONFIGURATION where game = :game")
        .bind("game", gameId)
        .mapTo<DbConfigurationMapper>()
        .singleOrNull()
}

private fun getDbShipMapper(handle: Handle, gameId: Int): List<DbShipMapper> {
    return handle.createQuery("select * from SHIP where configuration = :configuration")
        .bind("configuration", gameId)
        .mapTo<DbShipMapper>()
        .toList()
}
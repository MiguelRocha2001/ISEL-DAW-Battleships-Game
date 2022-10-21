package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.ship.toShipType
import pt.isel.daw.dawbattleshipgame.domain.state.*
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase

internal fun fetchGameByUser(handle: Handle, userId: Int): Game? {
    val gameId = getGameIdBUser(handle, userId) ?: return null
    return fetchGameInternal(handle, gameId)
}

internal fun fetchGameInternal(handle: Handle, gameId: Int): Game? {
    val dbGameMapper = getDbGameMapper(handle, gameId) ?: return null
    val (player1DbBoardMapper, player2DbBoardMapper) = getDbBoardMapperMappers(handle, gameId)

    val player1DbPanelMapperList = getDbPanelMapperMappers(handle, gameId, dbGameMapper.player1)
    val player2DbPanelMapperList = getDbPanelMapperMappers(handle, gameId, dbGameMapper.player2)

    val dbConfigurationMapper = getDbConfigurationMapper(handle, gameId) ?: throw IllegalStateException("Game $gameId has no configuration")
    val dbShipMapperList = getDbShipMapper(handle, gameId)

    val configuration = buildConfiguration(dbConfigurationMapper, dbShipMapperList)
    val player1Board = buildBoard(player1DbPanelMapperList, dbConfigurationMapper.board_size)
    val player2Board = buildBoard(player2DbPanelMapperList, dbConfigurationMapper.board_size)

    // if game is finished, return it
    if (dbGameMapper.winner != null) {
        return FinishedPhase(
            dbGameMapper.id,
            configuration,
            dbGameMapper.player1,
            dbGameMapper.player2,
            player1Board,
            player2Board,
            winner = dbGameMapper.winner
        )
    }
    else {
        if (dbGameMapper.player_turn != null) {
            return BattlePhase(
                configuration,
                gameId,
                dbGameMapper.player1,
                dbGameMapper.player2,
                player1Board,
                player2Board,
            )
        }
        else {
            val player1Game = if (player1DbBoardMapper.confirmed)
                PlayerWaitingPhase(gameId, configuration, player1Board, dbGameMapper.player1)
            else
                PlayerPreparationPhase(gameId, configuration, dbGameMapper.player1, player1Board)

            val player2Game = if (player2DbBoardMapper.confirmed)
                    PlayerWaitingPhase(gameId, configuration, player2Board, dbGameMapper.player2)
            else
                PlayerPreparationPhase(gameId, configuration, dbGameMapper.player2, player2Board)

            return SinglePhase(gameId, configuration, dbGameMapper.player1, dbGameMapper.player2, player1Game, player2Game)
        }
    }
}

private fun getGameIdBUser(handle: Handle, userId: Int): Int? {
    val dbGameMapper = handle.createQuery(
        """
            SELECT * FROM game
            WHERE player1 = :userId OR player2 = :userId
        """.trimIndent()
    )
        .bind("userId", userId)
        .mapTo<DbGameMapper>()
        .firstOrNull() ?: return null
    return dbGameMapper.id
}

private fun buildBoard(dbPanelMapperList: List<DbPanelMapper>, gameDim : Int): Board {
    return Board(gameDim).placePanels(dbPanelMapperList.map { it.toPanel() })
}

private fun buildConfiguration(dbConfigurationMapper: DbConfigurationMapper, dbShipMapperList: List<DbShipMapper>): Configuration {
    return Configuration(
        dbConfigurationMapper.board_size,
        dbShipMapperList.map { it.name.toShipType() to it.length }.toSet(),
        dbConfigurationMapper.n_shots,
        dbConfigurationMapper.timeout
    )
}

private fun getDbGameMapper(handle: Handle, gameId: Int): DbGameMapper? {
    return handle.createQuery("select * from GAME where id = :id")
        .bind("id", gameId)
        .mapTo<DbGameMapper>()
        .singleOrNull()
}

private fun getDbBoardMapperMappers(handle: Handle, gameId: Int): Pair<DbBoardMapper, DbBoardMapper> {
    val boards = handle.createQuery("select * from BOARD where game = :game")
        .bind("game", gameId)
        .mapTo<DbBoardMapper>()
        .toList()
    if (boards.size != 2) {
        throw IllegalStateException("Game $gameId has ${boards.size} boards")
    }
    return Pair(boards[0], boards[1])
}

private fun getDbPanelMapperMappers(handle: Handle, gameId: Int, userId: Int): List<DbPanelMapper> {
    return handle.createQuery("select * from PANEL where game = :game and _user = :_user")
        .bind("game", gameId)
        .bind("_user", userId)
        .mapTo<DbPanelMapper>()
        .toList()
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
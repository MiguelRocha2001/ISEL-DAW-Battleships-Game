package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.*
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.ship.toShipType
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerState


internal fun fetchGameByUser(handle: Handle, userId: Int): Game? {
    val gameId = getGameIdBUser(handle, userId) ?: return null
    return fetchGameInternal(handle, gameId)
}

internal fun fetchGameInternal(handle: Handle, gameId: Int): Game? {
    val dbGameMapper = getDbGameMapper(handle, gameId) ?: return null
    val (player1DbBoardMapper, player2DbBoardMapper) = getDbBoardMapperMappers(handle, gameId)

    val player1DbPanelMapperList = getDbPanelMapperMappers(handle, gameId, dbGameMapper.player1)
    val player2DbPanelMapperList = getDbPanelMapperMappers(handle, gameId, dbGameMapper.player2)

    val dbConfigurationMapper = getDbConfigurationMapper(handle, gameId) ?:
        throw IllegalStateException("Game $gameId has no configuration")

    val configuration = buildConfiguration(dbConfigurationMapper, getDbShipMapper(handle, gameId))
    val player1Board = buildBoard(player1DbPanelMapperList, dbConfigurationMapper.board_size)
    val player2Board = buildBoard(player2DbPanelMapperList, dbConfigurationMapper.board_size)


    when(getGameState(dbGameMapper)){
        GameState.FINISHED -> {
            return getEndPhase(dbGameMapper, configuration, player1Board, player2Board)
        }
        GameState.BATTLE -> {
            return getBattlePhase(dbGameMapper, configuration, player1Board, player2Board)
        }
        GameState.FLEET_SETUP -> {
            val player1Game = getPlayerPhase(dbGameMapper, configuration, player1Board, player1DbBoardMapper)
            val player2Game = getPlayerPhase(dbGameMapper, configuration, player2Board, player2DbBoardMapper)
            return SinglePhase(gameId, configuration,
                dbGameMapper.player1, dbGameMapper.player2,
                player1Game, player2Game
            )
        }
        else -> return null
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

private fun buildConfiguration(
    dbConfigurationMapper: DbConfigurationMapper,
    dbShipMapperList: List<DbShipMapper>
): Configuration {
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

private fun getGameState(g : DbGameMapper): GameState {
    return if(g.winner != null) GameState.FINISHED
    else if(g.player_turn != null) GameState.BATTLE
    else GameState.FLEET_SETUP
}

private fun getEndPhase(
    dbGameMapper: DbGameMapper, configuration: Configuration,
    p1Board: Board, p2Board: Board
) = requireNotNull(dbGameMapper.winner).run { FinishedPhase(
    dbGameMapper.id, configuration, dbGameMapper.player1,
    dbGameMapper.player2, p1Board, p2Board, dbGameMapper.winner
) }

private fun getBattlePhase(
    dbGameMapper: DbGameMapper, configuration: Configuration,
    p1Board: Board, p2Board: Board
) = requireNotNull(dbGameMapper.player_turn).run { BattlePhase(
    configuration, dbGameMapper.id, dbGameMapper.player1,
    dbGameMapper.player2, p1Board, p2Board
) }

private fun getPlayerPhase(
    dbGameMapper: DbGameMapper, configuration: Configuration,
    pBoard: Board, boardMapper: DbBoardMapper
) = PlayerPhase(
    dbGameMapper.id,configuration, boardMapper._user, pBoard,
        if(boardMapper.confirmed) PlayerState.WAITING
        else PlayerState.PREPARATION
)
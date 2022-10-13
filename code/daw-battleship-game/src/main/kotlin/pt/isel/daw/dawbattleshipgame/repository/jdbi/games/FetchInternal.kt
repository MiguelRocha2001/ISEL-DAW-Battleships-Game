package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import pt.isel.daw.dawbattleshipgame.domain.board.ShipPanel
import pt.isel.daw.dawbattleshipgame.domain.board.WaterPanel
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.toShipType

internal fun fetchGameInternal(handle: Handle, gameId: Int): Game? {
    val dbGameMapper = getDbGameMapper(handle, gameId) ?: return null
    val (player1DbBoardMapper, player2DbBoardMapper) = getDbBoardMapperMappers(handle, gameId) ?: throw IllegalStateException("Game $gameId has no boards")
    val (player1DbPanelMapperList, player2DbPanelMapperList) = getDbPanelMapperMappers(handle, gameId) ?: throw IllegalStateException("Game $gameId has no panels")
    val dbConfigurationMapper = getDbConfigurationMapper(handle, gameId) ?: throw IllegalStateException("Game $gameId has no configuration")
    val dbShipMapperList = getDbShipMapper(handle, gameId) ?: throw IllegalStateException("Configuration associated with Game $gameId has no ships")

    val configuration = buildConfiguration(dbConfigurationMapper, dbShipMapperList)
    val player1Board = buildBoard(player1DbPanelMapperList)
    val player2Board = buildBoard(player2DbPanelMapperList)

    // if game is finished, return it
    if (dbGameMapper.finished) {
        return EndPhase(
            dbGameMapper.id,
            configuration,
            dbGameMapper.user1,
            dbGameMapper.user2,
            player1Board,
            player2Board
        )
    }
    else {
        when {
            player1DbBoardMapper.confirmed && player2DbBoardMapper.confirmed ->
                return WaitingPhase(
                    gameId,
                    dbGameMapper.user1,
                    dbGameMapper.user2,
                    player1Board,
                    player2Board,
                    configuration
                )
            !player1DbBoardMapper.confirmed && !player2DbBoardMapper.confirmed ->
                return PreparationPhase(
                    gameId,
                    configuration,
                    dbGameMapper.user1,
                    dbGameMapper.user2,
                    PlayerPreparationPhase(
                        gameId,
                        configuration,
                        dbGameMapper.user1,
                        player1Board
                    ),
                    PlayerPreparationPhase(
                        gameId,
                        configuration,
                        dbGameMapper.user2,
                        player2Board
                    )
                )
        }
    }
    throw NotImplementedError("Other states still not supported")
}

private fun buildBoard(dbPanelMapperList: List<DbPanelMapper>): Board {
    val panels = dbPanelMapperList
        .sortedBy { it.idx }
        .map { dbPanelMapper ->
        val isHit = dbPanelMapper.is_hit
        if (dbPanelMapper.type == "water") WaterPanel(isHit) else ShipPanel(dbPanelMapper.type.toShipType(), isHit)
    }
    return Board(panels)
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

private fun getDbPanelMapperMappers(handle: Handle, gameId: Int): Pair<List<DbPanelMapper>, List<DbPanelMapper>> {
    val user1Panels = handle.createQuery("select * from PANEL where game = :game and _user = :user")
        .bind("game", gameId)
        .bind("user", "user1")
        .mapTo<DbPanelMapper>()
        .toList()
    val user2Panels = handle.createQuery("select * from PANEL where game = :game and _user = :user")
        .bind("game", gameId)
        .bind("user", "user2")
        .mapTo<DbPanelMapper>()
        .toList()
    return Pair(user1Panels, user2Panels)
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
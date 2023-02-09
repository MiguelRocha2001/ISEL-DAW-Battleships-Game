package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.InitGame

internal fun insertGame(handle: Handle, game: Game) {
    handle.createUpdate(
        """
                insert into GAME(id, player1, player2, state, winner, player_turn, created, updated, deadline)
                values(:id, :player1, :player2, :state, :winner, :player_turn, :created, :updated, :deadline) 
            """.trimMargin()
    )
        .bind("id", game.id)
        .bind("player1", game.player1)
        .bind("state", game.state.dbName)
        .bind("player2", game.player2)
        .bind("winner", game.winner)
        .bind("player_turn", game.playerTurn)
        .bind("created", game.instants.created.epochSecond)
        .bind("updated", game.instants.updated.epochSecond)
        .bind("deadline", game.instants.deadline.epochSecond)
        .execute()
}

internal fun makeGame(handle: Handle, game: InitGame): Int? {
    return handle.createUpdate(
            """
                insert into GAME(player1, player2)
                values(:player1, :player2) 
            """.trimMargin()
    )
            .bind("player1", game.player1)
            .bind("player2", game.player2)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .firstOrNull()
}

internal fun insertBoards(handle: Handle, game: Game) {
    insertBoard(handle, game.id, game.player1, game.board1)
    insertBoard(handle, game.id, game.player2, game.board2)
}

internal fun insertBoard(handle: Handle, gameId: Int, user: Int, board: Board) {
    handle.createUpdate(
        """
                     insert into BOARD(game, _user, confirmed, grid)
                     values(:game, :_user, :confirmed, :grid)
                    """.trimMargin()
    )
        .bind("game", gameId)
        .bind("_user", user)
        .bind("confirmed", board.isConfirmed())
        .bind("grid", board.getDbString())
        .execute()
}


fun insertConfiguration(handle: Handle, gameId: Int, configuration: Configuration) {
    handle.createUpdate(
        """
                        insert into CONFIGURATION(game, board_size, n_shots, timeout, fleet)
                        values(:game, :board_size, :n_shots, :timeout, :fleet)
                    """.trimMargin()
    )
        .bind("game", gameId)
        .bind("board_size", configuration.boardSize)
        .bind("n_shots", configuration.nShotsPerRound)
        .bind("timeout", configuration.roundTimeout)
        .bind("fleet", Configuration.mapper.writeValueAsString(configuration.fleet))
        .execute()
}

fun updateGame(handle: Handle, game: Game) {
    handle.createUpdate(
            """update game set state = :state, 
                    winner = :winner, player_turn = :playerTurn,
                    updated = :updated, deadline = :deadline 
                    where id = :id
                """.trimMargin()
    )
            .bind("id", game.id)
            .bind("state", game.state.dbName)
            .bind("winner", game.winner)
            .bind("playerTurn", game.playerTurn)
            .bind("updated", game.instants.updated.epochSecond)
            .bind("deadline", game.instants.deadline.epochSecond)
            .execute()
}

fun updateBoard(handle: Handle, board: Board, userId : Int, gameId: Int) {
    handle.createUpdate(
            """update board set grid = :grid, confirmed = :confirmed
                    where _user = :userId and game = :gameId
                """.trimMargin()
    )
            .bind("grid", board.getDbString())
            .bind("confirmed", board.isConfirmed())
            .bind("userId", userId)
            .bind("gameId", gameId)
            .execute()
}

fun deleteGame(handle: Handle, gameId: Int) {
    handle.createUpdate("""delete from CONFIGURATION where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from BOARD where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from GAME where id = :id""").bind("id", gameId).execute()
}

fun clearAllTables(handle: Handle) {
    handle.createUpdate("""delete from BOARD""").execute()
    handle.createUpdate("""delete from CONFIGURATION""").execute()
    handle.createUpdate("""delete from GAME""").execute()
}

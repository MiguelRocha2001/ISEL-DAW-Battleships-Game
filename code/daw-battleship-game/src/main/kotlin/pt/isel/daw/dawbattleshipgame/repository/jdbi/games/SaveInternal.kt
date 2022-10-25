package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.*

internal fun insertGame(handle: Handle, game: Game) {
    val winner = if (game is FinishedPhase) game.winner else null
    val playerTurn = if (game is BattlePhase) game.playersTurn else null
    val resultBearing = handle.createUpdate(
        """
                insert into GAME(id, player1, player2, winner, player_turn)
                values(:id, :player1, :player2, :winner, :player_turn) 
                returning id
            """
    )
        .bind("id", game.gameId)
        .bind("player1", game.player1)
        .bind("player2", game.player2)
        .bind("winner", winner)
        .bind("player_turn", playerTurn)
        .execute()
}

internal fun createGame(handle : Handle, player1Id: Int, player2Id : Int){
    TODO()
}

internal fun insertBoards(handle: Handle, game: Game) {
    val player1Board = when (game) {
        is SinglePhase -> game.player1Game.board
        is BattlePhase -> game.board1
        is FinishedPhase -> game.board1
    }

    val player2Board = when (game) {
        is SinglePhase -> game.player2Game.board
        is BattlePhase -> game.board2
        is FinishedPhase -> game.board2
    }
    insertBoard(handle, game.gameId, game.player1, player1Board)
    insertBoard(handle, game.gameId, game.player2, player2Board)
}

internal fun insertBoard(handle: Handle, gameId: Int, user: Int, board: Board) {
    handle.createUpdate(
        """
                        insert into BOARD(game, _user)
                        values(:game, :_user)
                    """
    )
        .bind("game", gameId)
        .bind("_user", user)
        .execute()
    insertPanel(handle, gameId, user, board.board)
}

fun insertPanel(handle: Handle, gameId: Int, user: Int, board: List<Panel>) {
    board.forEach{ panel ->
        handle.createUpdate(
            """
                        insert into PANEL(game, _user, x, y, is_hit, type)
                        values(:game, :_user, :x, :y, :is_hit, :type)
                    """
        )
            .bind("game", gameId)
            .bind("_user", user)
            .bind("x", panel.coordinate.row)
            .bind("y", panel.coordinate.column)
            .bind("is_hit", panel.isHit)
            .bind("type", panel.getType())
            .execute()
    }
}

fun insertConfiguration(handle: Handle, gameId: Int, configuration: Configuration) {
    handle.createUpdate(
        """
                        insert into CONFIGURATION(game, board_size, n_shots, timeout)
                        values(:game, :board_size, :n_shots, :timeout)
                    """
    )
        .bind("game", gameId)
        .bind("board_size", configuration.boardSize)
        .bind("n_shots", configuration.nShotsPerRound)
        .bind("timeout", configuration.roundTimeout)
        .execute()
    insertConfigurationShips(handle, gameId, configuration.fleet)
}

fun insertConfigurationShips(handle: Handle, gameId: Int, ships: Set<Pair<ShipType, Int>>) {
    ships.forEach { (shipType, length) ->
        handle.createUpdate(
            """
                        insert into SHIP(configuration, name, length)
                        values(:configuration, :name, :length)
                    """
        )
            .bind("configuration", gameId)
            .bind("name", shipType.name.lowercase())
            .bind("length", length)
            .execute()
    }
}

fun confirmBoard(handle: Handle, gameId: Int, playerId: Int) {
    handle.createUpdate(
        """
                update BOARD
                set confirmed = true
                where game = :game and user = :_user
                    """
    )
        .bind("game", gameId)
        .bind("user", playerId)
        .execute()
}

fun deleteGame(handle: Handle, gameId: Int) {
    handle.createUpdate("""delete from SHIP where configuration = :configuration""").bind("configuration", gameId).execute()
    handle.createUpdate("""delete from CONFIGURATION where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from PANEL where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from BOARD where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from GAME where id = :id""").bind("id", gameId).execute()
}

fun clearAllTables(handle: Handle) {
    handle.createUpdate("""delete from SHIP""").execute()
    handle.createUpdate("""delete from PANEL""").execute()
    handle.createUpdate("""delete from BOARD""").execute()
    handle.createUpdate("""delete from CONFIGURATION""").execute()
    handle.createUpdate("""delete from GAME""").execute()
}

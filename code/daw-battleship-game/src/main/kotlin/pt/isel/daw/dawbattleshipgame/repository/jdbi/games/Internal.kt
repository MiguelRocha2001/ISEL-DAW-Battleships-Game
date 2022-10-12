package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import pt.isel.daw.dawbattleshipgame.domain.board.ShipPanel
import pt.isel.daw.dawbattleshipgame.domain.board.WaterPanel
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

internal fun insertBoards(handle: Handle, game: Game) {
    val player1Board = when (game) {
        is PreparationPhase -> game.player1PreparationPhase.board
        is WaitingPhase -> game.player1WaitingPhase.board
        is BattlePhase -> game.player1Board
        is EndPhase -> game.player1Board
    }

    val player2Board = when (game) {
        is PreparationPhase -> game.player2PreparationPhase.board
        is WaitingPhase -> game.player2WaitingPhase.board
        is BattlePhase -> game.player2Board
        is EndPhase -> game.player2Board
    }
    insertBoard(handle, game.gameId, game.player1, player1Board)
    insertBoard(handle, game.gameId, game.player2, player2Board)
}

internal fun insertBoard(handle: Handle, gameId: Int, user: String, board: Board) {
    handle.createUpdate(
        """
                        insert into dbo.BOARD(game, user)
                        values(:game, :user)
                    """
    )
        .bind("game", gameId)
        .bind("_user", user)
    insertPanel(handle, gameId, user, board.board)
}

fun insertPanel(handle: Handle, gameId: Int, user: String, board: List<Panel>) {
    board.forEachIndexed { idx, panel ->
        val type = if (panel is ShipPanel) {
            when (panel.shipType) {
                ShipType.BATTLESHIP -> "battleship"
                ShipType.CARRIER -> "carrier"
                ShipType.DESTROYER -> "destroyer"
                ShipType.SUBMARINE -> "submarine"
                ShipType.CRUISER -> "cruiser"
            }
        } else "water"
        handle.createUpdate(
            """
                        insert into dbo.PANEL(game, user, idx, is_hit, type)
                        values(:game, :user, :idx, :is_hit, :type)
                    """
        )
            .bind("game", gameId)
            .bind("_user", user)
            .bind("idx", idx)
            .bind("is_hit", panel.isHit)
            .bind("type", type)
    }
}

internal fun insertGame(handle: Handle, game: Game) {
    handle.createUpdate(
        """
                        insert into dbo.GAME(id, user1, user2)
                        values(:id, :user1, :user2)
                    """
    )
        .bind("id", game.gameId)
        .bind("user1", game.player1)
        .bind("user2", game.player2)
    insertState(handle, game)
    if (game is BattlePhase) insertPlayerTurn(handle, game.playersTurn)
}

internal fun insertPlayerTurn(handle: Handle, playersTurn: String) {
    handle.createUpdate(
        """
                        insert into dbo.GAME(player_turn)
                        values(:player_turn)
                    """
    )
        .bind("player_turn", playersTurn)
}

internal fun insertState(handle: Handle, game: Game) {
    val state = when (game) {
        is PreparationPhase -> "preparation"
        is WaitingPhase -> "waiting"
        is BattlePhase -> "battle"
        is EndPhase -> "finished"
    }
    handle.createUpdate(
        """
                        insert into dbo.GAME(state)
                        values(:state)
                    """
    )
        .bind("state", state)
}
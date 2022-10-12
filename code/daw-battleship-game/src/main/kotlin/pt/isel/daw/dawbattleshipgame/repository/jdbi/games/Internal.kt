package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import pt.isel.daw.dawbattleshipgame.domain.board.ShipPanel
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
        .execute()
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
            .execute()
    }
}

internal fun insertGame(handle: Handle, game: Game) {
    val finished = game is EndPhase
    val playerTurn = if (game is BattlePhase) game.playersTurn else null
    handle.createUpdate(
        """
                        insert into dbo.GAME(id, user1, user2, finished, player_turn)
                        values(:id, :user1, :user2, :finished, :player_turn)
                    """
    )
        .bind("id", game.gameId)
        .bind("user1", game.player1)
        .bind("user2", game.player2)
        .bind("user2", finished)
        .bind("finished", finished)
        .bind("player_turn", playerTurn)
        .execute()
}

fun insertConfiguration(handle: Handle, gameId: Int, configuration: Configuration) {
    handle.createUpdate(
        """
                        insert into dbo.CONFIGURATION(game, board_size, n_shots, timeout)
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
                        insert into dbo.SHIP(configuration, name, length)
                        values(:configuration, :name, :length)
                    """
        )
            .bind("configuration", gameId)
            .bind("ship_type", shipType.name.lowercase())
            .bind("length", length)
            .execute()
    }
}

fun confirmBoard(handle: Handle, gameId: Int, playerId: String) {
    handle.createUpdate(
        """
                update dbo.BOARD
                set is_confirmed = true
                where game = :game and user = :_user
                    """
    )
        .bind("game", gameId)
        .bind("user", playerId)
        .execute()
}

fun deleteGame(handle: Handle, gameId: Int) {
    handle.createUpdate("""delete from dbo.SHIP where configuration = :configuration""").bind("configuration", gameId).execute()
    handle.createUpdate("""delete from dbo.CONFIGURATION where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from dbo.PANEL where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from dbo.BOARD where game = :game""").bind("game", gameId).execute()
    handle.createUpdate("""delete from dbo.GAME where id = :id""").bind("id", gameId)
}

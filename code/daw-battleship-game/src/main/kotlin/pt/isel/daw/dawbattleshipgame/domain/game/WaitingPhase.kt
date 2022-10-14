package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board

class WaitingPhase(
    override val gameId: Int,
    override val player1: Int,
    override val player2: Int,
    player1Board: Board,
    player2Board: Board,
    override val configuration: Configuration
) : Game() {

    val player1WaitingPhase: PlayerWaitingPhase
    val player2WaitingPhase: PlayerWaitingPhase

    init {
        this.player1WaitingPhase = PlayerWaitingPhase(gameId, configuration, player1Board, player1)
        this.player2WaitingPhase = PlayerWaitingPhase(gameId, configuration, player2Board, player2)
    }
}

class PlayerWaitingPhase(val gameId: Int, val configuration: Configuration, val board: Board, val playerId: Int)
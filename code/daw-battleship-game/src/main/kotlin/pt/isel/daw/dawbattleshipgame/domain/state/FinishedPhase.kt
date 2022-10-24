package pt.isel.daw.dawbattleshipgame.domain.state

import pt.isel.daw.dawbattleshipgame.domain.board.Board

class FinishedPhase(
    override val gameId: Int,
    override val configuration: Configuration,
    override val player1: Int,
    override val player2: Int,
    override val board1: Board,
    override val board2: Board,
    val winner: Int,
    override val state: GameState = GameState.FINISHED,
) : Game()
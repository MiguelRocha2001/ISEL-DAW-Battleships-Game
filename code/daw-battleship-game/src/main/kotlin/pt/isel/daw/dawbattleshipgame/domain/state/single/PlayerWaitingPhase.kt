package pt.isel.daw.dawbattleshipgame.domain.state.single

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration

class PlayerWaitingPhase(val gameId: Int, val configuration: Configuration, override val board: Board, val playerId: Int): Single()
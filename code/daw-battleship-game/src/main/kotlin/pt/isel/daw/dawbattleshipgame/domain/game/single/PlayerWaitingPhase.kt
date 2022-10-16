package pt.isel.daw.dawbattleshipgame.domain.game.single

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration

class PlayerWaitingPhase(val gameId: Int, val configuration: Configuration, override val board: Board, val playerId: Int): Single()
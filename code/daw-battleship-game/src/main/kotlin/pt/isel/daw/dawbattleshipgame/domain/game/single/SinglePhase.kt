package pt.isel.daw.dawbattleshipgame.domain.game.single

import pt.isel.daw.dawbattleshipgame.domain.board.Board

sealed class Single {
    abstract val board: Board
}
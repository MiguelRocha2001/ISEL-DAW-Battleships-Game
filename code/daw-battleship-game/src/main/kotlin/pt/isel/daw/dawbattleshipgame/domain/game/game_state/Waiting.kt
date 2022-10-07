package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet

class Waiting(
    override val configuration: Configuration,
    override val myBoard: Board,
) : GameState()
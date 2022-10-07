package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet

class End(
    override val configuration: Configuration,
    override val myBoard: Board,
    internal val opponentBoard: Board,
    override val playerShips: ShipSet
) : GameState()
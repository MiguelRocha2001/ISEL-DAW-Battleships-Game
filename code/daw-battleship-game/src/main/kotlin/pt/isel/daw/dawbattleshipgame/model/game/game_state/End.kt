package pt.isel.daw.dawbattleshipgame.model.game.game_state

import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Configuration
import pt.isel.daw.dawbattleshipgame.model.game.game_state.GameState
import pt.isel.daw.dawbattleshipgame.model.ship.ShipSet

class End(
    override val configuration: Configuration,
    override val myBoard: Board,
    internal val opponentBoard: Board,
    override val playerShips: ShipSet
) : GameState()
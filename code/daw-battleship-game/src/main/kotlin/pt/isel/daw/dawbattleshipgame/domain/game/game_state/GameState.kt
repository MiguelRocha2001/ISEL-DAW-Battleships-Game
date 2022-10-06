package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.Board
import pt.isel.daw.dawbattleshipgame.domain.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet

sealed class GameState {
    abstract val configuration: Configuration
    abstract val myBoard: Board
    abstract val playerShips: ShipSet
}
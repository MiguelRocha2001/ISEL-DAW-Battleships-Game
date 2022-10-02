package pt.isel.daw.dawbattleshipgame.model.game.game_state

import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Configuration
import pt.isel.daw.dawbattleshipgame.model.ship.ShipSet

sealed class GameState {
    abstract val configuration: Configuration
    abstract val myBoard: Board
    abstract val playerShips: ShipSet
}
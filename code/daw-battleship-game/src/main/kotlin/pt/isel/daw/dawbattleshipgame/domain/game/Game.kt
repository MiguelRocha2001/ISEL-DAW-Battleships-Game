package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration

    companion object {
        fun getNewGame(gameId: Int, player1Id: String, player2Id: String, configuration: Configuration) =
            PreparationPhase(gameId, player1Id, player2Id, configuration)
    }
}

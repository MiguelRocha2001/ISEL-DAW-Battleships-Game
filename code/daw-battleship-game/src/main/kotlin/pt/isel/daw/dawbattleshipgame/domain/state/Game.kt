package pt.isel.daw.dawbattleshipgame.domain.state

import org.jetbrains.annotations.TestOnly
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase
import java.util.*

enum class GameState {
    NOT_STARTED,
    FLEET_SETUP,
    WAITING,
    BATTLE,
    FINISHED;

    val dbName = this.name.lowercase(Locale.getDefault())
}

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration
    abstract val player1: Int
    abstract val player2: Int
    abstract val board1: Board
    abstract val board2: Board
    abstract val state: GameState

    companion object {
        fun newGame(gameId: Int, player1: Int, player2: Int, configuration: Configuration) =
            SinglePhase(
                gameId,
                configuration,
                player1,
                player2,
                PlayerPhase(gameId, configuration, player1),
                PlayerPhase(gameId, configuration, player2)
            )
    }
}
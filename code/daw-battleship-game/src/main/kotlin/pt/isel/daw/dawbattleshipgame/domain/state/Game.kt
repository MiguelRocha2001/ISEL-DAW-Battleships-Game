package pt.isel.daw.dawbattleshipgame.domain.state

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration
    abstract val player1: Int
    abstract val player2: Int

    companion object {
        fun newGame(gameId: Int, player1: Int, player2: Int, configuration: Configuration) =
            SinglePhase(
                gameId,
                configuration,
                player1,
                player2,
                PlayerPreparationPhase(gameId, configuration, player1),
                PlayerPreparationPhase(gameId, configuration, player2)
            )
    }
}
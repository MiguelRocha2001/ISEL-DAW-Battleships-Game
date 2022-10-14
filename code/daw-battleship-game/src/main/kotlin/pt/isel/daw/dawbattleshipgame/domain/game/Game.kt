package pt.isel.daw.dawbattleshipgame.domain.game

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration
    abstract val player1: Int
    abstract val player2: Int

    companion object {
        fun newGame(gameId: Int, player1: Int, player2: Int, configuration: Configuration) =
            PreparationPhase(gameId, configuration, player1, player2)
    }
}

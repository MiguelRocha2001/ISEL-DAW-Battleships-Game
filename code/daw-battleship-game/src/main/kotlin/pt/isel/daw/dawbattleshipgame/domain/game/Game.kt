package pt.isel.daw.dawbattleshipgame.domain.game

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration
    abstract val player1: String
    abstract val player2: String

    companion object {
        fun newGame(gameId: Int, player1: String, player2: String, configuration: Configuration) =
            PreparationPhase(gameId, configuration, player1, player2)
    }
}

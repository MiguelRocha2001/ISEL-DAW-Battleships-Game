package pt.isel.daw.dawbattleshipgame.domain.state

<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
=======
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase
>>>>>>> Stashed changes

enum class GameState { FLEET_SETUP, WAITING, BATTLE, FINISHED }

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
<<<<<<< Updated upstream
                PlayerPreparationPhase(gameId, configuration, player1),
                PlayerPreparationPhase(gameId, configuration, player2)
=======
                PlayerPhase(gameId, configuration, player1),
                PlayerPhase(gameId, configuration, player2)
>>>>>>> Stashed changes
            )
    }
}
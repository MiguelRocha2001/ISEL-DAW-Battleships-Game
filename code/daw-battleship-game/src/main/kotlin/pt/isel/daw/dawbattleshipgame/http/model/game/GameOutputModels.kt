package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.GameState
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction

data class GameStartOutputModel(
    val action: SirenAction
)

data class BoardOutputModel(val board: Board)

data class GameIdOutputModel(
    val clazz: String = "Game",
    val properties: List<Pair<String, String>>,
    val actions: List<SirenAction>,
)

enum class GameStateOutputModel {
    FLEET_SETUP,
    WAITING,
    BATTLE,
    FINISHED;

    @JsonValue
    fun getName() = name.lowercase()

    companion object {
        fun get(value: GameState) = when (value) {
            GameState.FLEET_SETUP -> FLEET_SETUP
            GameState.WAITING -> WAITING
            GameState.BATTLE -> BATTLE
            GameState.FINISHED -> FINISHED
        }
    }
}
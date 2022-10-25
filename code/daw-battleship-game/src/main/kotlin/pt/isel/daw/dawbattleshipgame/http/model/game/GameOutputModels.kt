package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.GameState

data class GameProperties(val gameState: GameStateOutputModel)

data class BoardOutputModel(
    val cells: Map<CoordinateModel, Pair<String, Boolean>>,
    val nCells: Int
)

fun Board.toBoardOutputModel(): BoardOutputModel {
    val cells = mutableMapOf<CoordinateModel, Pair<String, Boolean>>()
    this.board.forEach { panel ->
        val coordinateModel = CoordinateModel(panel.coordinate.row, panel.coordinate.column)
        val shipType = panel.shipType
        val isHit = panel.isHit
        cells[coordinateModel] = Pair(shipType.toString().lowercase(), isHit)
    }
    return BoardOutputModel(cells, this.board.size)
}

data class CoordinateModel(
    val l: Int,
    val c: Int
)

data class GameIdOutputModel(val gameId: Int)

enum class GameStateOutputModel {
    NOT_STARTED,
    FLEET_SETUP,
    WAITING,
    BATTLE,
    FINISHED;

    @JsonValue
    fun getName() = name.lowercase()

    companion object {
        fun get(value: GameState) = when (value) {
            GameState.NOT_STARTED -> NOT_STARTED
            GameState.FLEET_SETUP -> FLEET_SETUP
            GameState.WAITING -> WAITING
            GameState.BATTLE -> BATTLE
            GameState.FINISHED -> FINISHED
        }
    }
}

data class GameOutputModel(
    val gameId: Int,
    val configuration: Configuration,
    val player1: Int,
    val player2: Int,
    val state: GameStateOutputModel,
    val board1: BoardOutputModel,
    val board2: BoardOutputModel,
)
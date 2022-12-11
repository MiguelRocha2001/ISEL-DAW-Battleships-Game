package pt.isel.daw.dawbattleshipgame.http.model.game

import com.fasterxml.jackson.annotation.JsonValue
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.player.Player

/**
 * Represents a list of Users, with their respective number of games played and score
 */
data class UserStatsOutputModel(val users: List<UserStatOutputModel>)
data class UserStatOutputModel(val username: String, val wins: Int, val gamesPlayed: Int)



data class GameInfoOutputModel(val state: GameStateOutputModel, val gameId: Int?)

data class PlaceShipsOutputModel(val shipIds: List<Int>)


fun Board.toBoardOutputModel(): BoardOutputModel {
    /* Disabled temporary
    val cells = mutableMapOf<CoordinateModel, CoordinateContentOutputModel>()
    this.board.forEach { panel ->
        val coordinateModel = CoordinateModel(panel.coordinate.row, panel.coordinate.column)
        val shipType = panel.shipType
        val isHit = panel.isHit
        cells[coordinateModel] = CoordinateContentOutputModel(shipType.toString().lowercase(), isHit)
    }
    return BoardOutputModel(cells, this.board.size)
    */
    return BoardOutputModel(this.getDbString(), this.board.size, this.isConfirmed())
}

data class BoardOutputModel(
    val cells: String, // Board representation
    val nCells: Int,
    val isConfirmed: Boolean
)

data class CoordinateModel(
    val l: Int,
    val c: Int
)

data class CoordinateContentOutputModel(val shipType: String, val isHit: Boolean)

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
    val myPlayer: PlayerOutputModel
)

enum class PlayerOutputModel {
    ONE, TWO;

    @JsonValue
    fun getName() = name.lowercase()

    companion object {
        fun get(value: Player) = when (value) {
            Player.ONE -> ONE
            Player.TWO -> TWO
        }
    }
}
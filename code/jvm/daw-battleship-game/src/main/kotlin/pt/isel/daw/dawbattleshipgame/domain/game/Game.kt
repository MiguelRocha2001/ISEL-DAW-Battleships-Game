package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.GameState.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import java.util.*

enum class GameState {
    NOT_STARTED,
    FLEET_SETUP,
    WAITING,
    BATTLE,
    FINISHED;

    val dbName = this.name.lowercase(Locale.getDefault())
}


fun String.getDbState() =
    GameState.values().first { it.dbName == this }

class Game(
    val gameId: Int,
    val configuration: Configuration,
    val player1: Int,
    val player2: Int,
    val board1: Board,
    val board2: Board,
    val state: GameState,

    val playerTurn: Int? =
        if (state == NOT_STARTED || state == FLEET_SETUP)
            null else player1,

    val winner: Int? = null
) {
    init {
        when (state) {
            NOT_STARTED -> {
                requireNull(playerTurn); requireNull(winner)
            }
            FLEET_SETUP -> {
                requireNull(playerTurn); requireNull(winner)
            }
            WAITING -> {
                requireNull(playerTurn); requireNull(winner)
            }
            BATTLE -> {
                requireNotNull(playerTurn); requireNull(winner)
            }
            FINISHED -> {
                requireNotNull(playerTurn); requireNotNull(winner)
            }
        }
    }

    internal fun setWinner(winner: Int) =
        Game(
            gameId, configuration, player1,
            player2, board1, board2, FINISHED,
            playerTurn, winner
        )

    internal fun updateBoard(board: Board, player: Player, state: GameState = FLEET_SETUP) =
        require(state == FLEET_SETUP || state == BATTLE).let {
            when (player) {
                Player.ONE -> Game(gameId, configuration, player1, player2, board, board2, state)
                Player.TWO -> Game(gameId, configuration, player1, player2, board1, board, state)
            }
        }

    internal fun switchTurn() = Game(
        gameId, configuration,
        player1, player2, board1, board2, state,
        changePlayersTurn()
    )

    private fun changePlayersTurn() =
        when (playerTurn) {
            player1 -> player2
            player2 -> player1
            else -> throw IllegalStateException("Illegal game state")
        }

    fun getBoard(player: Player = Player.ONE) =
        when (player) {
            Player.ONE -> board1
            Player.TWO -> board2
        }

    companion object {
        fun newGame(gameId: Int, player1: Int, player2: Int, configuration: Configuration) =
            Game(
                gameId,
                configuration,
                player1,
                player2,
                Board(configuration.boardSize),
                Board(configuration.boardSize),
                FLEET_SETUP
            )
    }

    //TODO() to be changed, its like this because of the tests
    override fun toString(): String = board1.toString()
}
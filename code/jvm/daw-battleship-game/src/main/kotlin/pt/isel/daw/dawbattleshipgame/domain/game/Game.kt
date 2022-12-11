package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.GameState.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.utils.RealClock
import java.time.Duration
import java.time.Instant
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

class InitGame(val player1 : Int, val player2: Int, val configuration : Configuration){
    val board1 = Board(configuration.boardSize)
    val board2 = Board(configuration.boardSize)
}
data class Instants(
        val created : Instant = RealClock.now(),
        val updated : Instant = RealClock.now(),
        val deadline: Instant = RealClock.now(),
){
    companion object{
        fun get(created : Long, updated : Long, deadline : Long) =
                Instants(
                        Instant.ofEpochSecond(created),
                        Instant.ofEpochSecond(updated),
                        Instant.ofEpochSecond(deadline),
                )
    }
}


class Game (
        val id: Int,
        val configuration: Configuration,
        val player1: Int,
        val player2: Int,
        val board1: Board,
        val board2: Board,
        val state: GameState = NOT_STARTED,
        val instants: Instants = Instants(),

        val playerTurn: Int? =
        if (state == NOT_STARTED || state == FLEET_SETUP)
            null else player1,

        val winner: Int? = null
) {
    fun debug() = println(
            "Game(id = $id, " +
                    "configuration = $configuration, " +
                    "player1 = $player1, " +
                    "player2 = $player2, " +
                    "board1 = $board1, " +
                    "board2 = $board2, " +
                    "state = $state)"
    )

    init {
        when (state) {
            NOT_STARTED -> {
                checkNull(playerTurn); checkNull(winner)
            }
            FLEET_SETUP -> {
                checkNull(playerTurn); checkNull(winner)
            }
            WAITING -> {
                checkNull(playerTurn); checkNull(winner)
            }
            BATTLE -> {
                checkNotNull(playerTurn); checkNull(winner)
            }
            FINISHED -> {
                checkNotNull(playerTurn); checkNotNull(winner)
            }
        }
    }

    internal fun setWinner(winner: Int) =
        Game(
            id, configuration, player1,
            player2, board1, board2, FINISHED,
            instants, playerTurn, winner
        )

    fun getUser(userId : Int) = when(userId) {
        player1 -> Player.ONE
        player2 -> Player.TWO
        else -> throw IllegalArgumentException("No user found")
    }

    internal fun updateGame(board: Board, player: Player, playerTurn: Int?, state: GameState = FLEET_SETUP) =
        require(state == FLEET_SETUP || state == BATTLE).let {
            when (player) {
                Player.ONE -> Game(
                        id, configuration,
                        player1, player2, board,
                        board2, state, instants,
                        playerTurn, winner
                )
                Player.TWO -> Game(
                        id, configuration,
                        player1, player2, board1,
                        board, state, instants,
                        playerTurn, winner
                )
            }
        }

    fun getBoard(player: Player = Player.ONE) =
        when (player) {
            Player.ONE -> board1
            Player.TWO -> board2
        }

    /**
     * Check if all ships have been placed
     * @return true if all ships placed, false otherwise
     */
    fun allShipsPlaced() =
            configuration.fleet.toMap().let {
                board1.allShipsPlaced(it) &&
                        board2.allShipsPlaced(it)
            }

    companion object {
        fun newGame(gameId: Int, player1: Int, player2: Int,
                    configuration: Configuration,
                    instant: Instant = RealClock.now()
        ) = Game(
                gameId, configuration,
                player1, player2,
                Board(configuration.boardSize),
                Board(configuration.boardSize),
                FLEET_SETUP, Instants(instant, instant,
                instant + Duration.ofSeconds(configuration.roundTimeout)
        ))

        fun startGame(player1: Int, player2: Int, configuration: Configuration) =
                InitGame(player1, player2, configuration)
    }

    internal fun getPlayerId(player: Player) =
        when (player) {
            Player.ONE -> player1
            Player.TWO -> player2
        }

    //TODO(to be changed, its like this because of the tests)
    override fun toString(): String = board1.toString()
}
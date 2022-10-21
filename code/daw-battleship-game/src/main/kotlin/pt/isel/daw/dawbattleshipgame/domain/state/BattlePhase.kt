package pt.isel.daw.dawbattleshipgame.domain.state

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate

class BattlePhase: Game {
    override val gameId: Int
    override val configuration: Configuration

    override val player1: Int // user1 Id
    override val player2: Int // user2 Id

    override val board1: Board
    override val board2: Board

    override val state: GameState = GameState.BATTLE

    val playersTurn: Int //user ID

    constructor(
        configuration: Configuration,
        gameId: Int,
        playerA: Int,
        playerB: Int,
        boardA: Board,
        boardB: Board
    ) {
        this.gameId = gameId
        this.configuration = configuration
        this.player1 = playerA
        this.player2 = playerB
        this.board1 = boardA
        this.board2 = boardB
        this.playersTurn = player1 // always starts with player1
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     */
    private constructor(old: BattlePhase, player: Int, shot: Coordinate) {
        gameId = old.gameId
        configuration = old.configuration
        player1 = old.player1
        player2 = old.player2

        if (player == player1) {
            board1 = old.board1
            board2 = old.board2.placeShot(shot)
        } else {
            board1 = old.board1.placeShot(shot)
            board2 = old.board2
        }
        playersTurn = if (old.playersTurn == player1) player2
        else player1
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
     */
    fun tryPlaceShot(userId: Int, shot: Coordinate): Game? {
        return try {
            val gameResult = BattlePhase(this, userId, shot)
            if (gameResult.board1.isGameOver() || gameResult.board2.isGameOver()) {
                FinishedPhase(gameId, configuration, player1, player2, board1, board2, winner = userId)
            } else {
                gameResult
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun Board.isGameOver() = this.getShips().all { it.isSunk }

}
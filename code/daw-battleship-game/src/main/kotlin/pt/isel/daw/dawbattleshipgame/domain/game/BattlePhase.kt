package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate

class BattlePhase: Game {
    override val gameId: Int
    override val configuration: Configuration

    override val player1: Int // user1 Id
    override val player2: Int // user2 Id

    val player1Board: Board
    val player2Board: Board

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
        this.player1Board = boardA
        this.player2Board = boardB
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
            player1Board = old.player1Board
            player2Board = old.player2Board.hitPanel(shot) ?: throw Exception("Invalid shot")
        } else {
            player1Board = old.player1Board.hitPanel(shot) ?: throw Exception("Invalid shot")
            player2Board = old.player2Board
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
            if (gameResult.player1Board.isGameOver() || gameResult.player2Board.isGameOver()) {
                EndPhase(gameId, configuration, player1, player2, player1Board, player2Board)
            } else {
                gameResult
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun Board.isGameOver(): Boolean {
        val hitPanels = this.getHitCoordinates()
        val shipPanels = this.getShipCoordinates()
        return hitPanels.containsAll(shipPanels)
    }
}
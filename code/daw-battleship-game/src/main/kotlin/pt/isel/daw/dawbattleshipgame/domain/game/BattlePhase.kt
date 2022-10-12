package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate

class BattlePhase: Game {
    override val gameId: Int
    override val configuration: Configuration

    override val player1: String // user1 Id
    override val player2: String // user2 Id

    private val player1Board: Board
    private val player2Board: Board

    private val playersTurn: String

    constructor(
        configuration: Configuration,
        gameId: Int,
        playerA: String,
        playerB: String,
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
    private constructor(old: BattlePhase, player: String, shot: Coordinate) {
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
    fun tryPlaceShot(token: String, shot: Coordinate): Game? {
        return try {
            val gameResult = BattlePhase(this, token, shot)
            if (gameResult.player1Board.isGameOver() || gameResult.player2Board.isGameOver()) {
                EndPhase(gameId, configuration)
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
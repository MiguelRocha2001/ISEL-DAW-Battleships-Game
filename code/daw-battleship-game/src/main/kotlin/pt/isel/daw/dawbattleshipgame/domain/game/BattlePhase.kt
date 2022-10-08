package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.End

class BattlePhase: Game {
    override val gameId: Int
    override val configuration: Configuration

    private val playerA: String // user1 Id
    private val playerB: String // user2 Id

    private val boardA: Board
    private val boardB: Board

    private val isPlayerA: Boolean

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
        this.playerA = playerA
        this.playerB = playerB
        this.boardA = boardA
        this.boardB = boardB
        this.isPlayerA = true // always starts with playerA
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     */
    private constructor(old: BattlePhase, shot: Coordinate) {
        gameId = old.gameId
        configuration = old.configuration
        playerA = old.playerA
        playerB = old.playerB

        if (old.isPlayerA) {
            boardA = old.boardA
            boardB = old.boardB.hitPanel(shot) ?: throw Exception("Invalid shot")
        } else {
            boardA = old.boardA.hitPanel(shot) ?: throw Exception("Invalid shot")
            boardB = old.boardB
        }
        isPlayerA = !old.isPlayerA
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
     */
    override fun tryPlaceShot(shot: Coordinate): BattlePhase? {
        return try {
            val gameResult = BattlePhase(this, shot)
            if (gameResult.opponentBoard.isGameOver()) {
                End(configuration, board, opponentBoard)
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

    fun isMyTurn(token: String) =
        (isPlayerA && token == playerA) || (!isPlayerA && token == playerB)

}
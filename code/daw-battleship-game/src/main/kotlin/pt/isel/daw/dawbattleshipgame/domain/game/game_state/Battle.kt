package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet

class Battle: GameState {
    override val configuration: Configuration
    /** The player that is playing this turn */
    private val isMyTurn: Boolean
    override val myBoard: Board
    internal val opponentBoard: Board

    constructor(configuration: Configuration, myBoard: Board, opponentBoard: Board, isMyTurn: Boolean) {
        this.configuration = configuration
        this.isMyTurn = isMyTurn
        this.myBoard = myBoard
        this.opponentBoard = opponentBoard
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     */
    private constructor(old: Battle, shot: Coordinate) {
        configuration = old.configuration
        isMyTurn = !old.isMyTurn
        myBoard = old.myBoard
        opponentBoard = old.opponentBoard.hitPanel(shot) ?: throw Exception("Invalid shot")
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
     */
    fun tryPlaceShot(shot: Coordinate): GameState? {
        if (!isMyTurn) return null
        return try {
            val gameResult = Battle(this, shot)
            if (gameResult.opponentBoard.isGameOver()) {
                End(configuration, myBoard, opponentBoard)
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
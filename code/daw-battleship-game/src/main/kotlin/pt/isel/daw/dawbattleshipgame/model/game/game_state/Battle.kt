package pt.isel.daw.dawbattleshipgame.model.game.game_state

import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Configuration
import pt.isel.daw.dawbattleshipgame.model.Coordinate
import pt.isel.daw.dawbattleshipgame.model.ship.ShipSet

class Battle: GameState {
    override val configuration: Configuration
    override val myBoard: Board
    override val playerShips : ShipSet

    internal val opponentBoard: Board

    constructor(configuration: Configuration, myBoard: Board, playerShips: ShipSet, opponentBoard: Board) {
        this.configuration = configuration
        this.myBoard = myBoard
        this.playerShips = playerShips
        this.opponentBoard = opponentBoard
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     */
    private constructor(old: Battle, shot: Coordinate) {
        configuration = old.configuration
        myBoard = old.myBoard
        opponentBoard = old.opponentBoard.hitPanel(shot) ?: throw Exception("Invalid shot")
        playerShips = old.playerShips
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
     */
    fun tryPlaceShot(shot: Coordinate): GameState? {
        return try {
            val gameResult = Battle(this, shot)
            if (gameResult.opponentBoard.isGameOver()) {
                End(configuration, myBoard, opponentBoard, playerShips)
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
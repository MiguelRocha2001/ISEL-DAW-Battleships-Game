package pt.isel.daw.dawbattleshipgame.domain.game.game_state

import pt.isel.daw.dawbattleshipgame.domain.Board
import pt.isel.daw.dawbattleshipgame.domain.Configuration
import pt.isel.daw.dawbattleshipgame.domain.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipSet

class Battle: GameState {
    override val configuration: Configuration
    private val curPlayer: Player
    override val myBoard: Board
    override val playerShips : ShipSet

    internal val opponentBoard: Board

    constructor(configuration: Configuration, myBoard: Board, playerShips: ShipSet, opponentBoard: Board) {
        this.configuration = configuration
        curPlayer = Player.Player1
        this.myBoard = myBoard
        this.playerShips = playerShips
        this.opponentBoard = opponentBoard
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     */
    private constructor(old: Battle, shot: Coordinate) {
        configuration = old.configuration
        curPlayer = old.curPlayer.other()
        myBoard = old.myBoard
        opponentBoard = old.opponentBoard.hitPanel(shot) ?: throw Exception("Invalid shot")
        playerShips = old.playerShips
    }

    /**
     * Builds a new Game object, with place shot on [shot], in opponent board.
     * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
     */
    fun tryPlaceShot(shot: Coordinate, player: Player): GameState? {
        if (player != curPlayer) return null
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
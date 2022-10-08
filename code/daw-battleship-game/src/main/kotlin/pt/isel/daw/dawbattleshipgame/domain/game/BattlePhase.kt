package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.Battle
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class BattlePhase(val id: Int): Game(id) {
    private val gameState: Battle

    private constructor(old: BattlePhase, newGameState: GameState) {
        gameState = newGameState
        id = old.id
        playerId = old.playerId
    }

    override fun tryPlaceShip(ship: ShipType, position: Coordinate, orientation: Orientation): Game? {
        return null
    }

    override fun tryMoveShip(position: Coordinate, destination: Coordinate): Game? {
        return null
    }

    override fun tryPlaceShot(c: Coordinate): Game? {
        val gameStateResult = gameState.tryPlaceShot(c) ?: return null
        return Game(this, gameStateResult)
    }

    override fun tryConfirmFleet(): Game? {
        return null
    }

    override fun tryRotateShip(position: Coordinate): Game? {
        return null
    }

    override fun tryRotateShip(position: String): Game? {
        return null
    }

    override fun isShip(it: Coordinate): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateShips(): Game? {
        return null
    }

}
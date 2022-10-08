package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game {
    abstract val gameId: Int
    abstract val configuration: Configuration

    open fun tryPlaceShip(ship: ShipType, position: Coordinate, orientation: Orientation): Game? = null

    open fun tryMoveShip(position: Coordinate, destination: Coordinate): Game? = null

    open fun tryPlaceShot(c: Coordinate): Game? = null

    /**
     * Builds a new Game object, with the fleet confirmed.
     * This function will result in a new Game object, with the state changed to BATTLE.
     * From this point, it is not possible to place/move/rotate new ships.
     */
    open fun tryConfirmFleet(): Game? = null

    open fun tryRotateShip(position: Coordinate): Game? = null

    open fun tryRotateShip(position: String): Game? = null

    open fun isShip(it: Coordinate): Boolean = false

    open fun generateShips() : Game? = null
}

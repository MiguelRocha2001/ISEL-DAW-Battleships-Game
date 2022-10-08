package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinates
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.game.game_state.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import kotlin.random.Random

enum class State { WARMUP, WAITING, BATTLE, END }

sealed class Game(val id: Int) {
    abstract fun tryPlaceShip(
        ship: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): Game?

    abstract fun tryMoveShip(position: Coordinate, destination: Coordinate): Game?

    abstract fun tryPlaceShot(c: Coordinate): Game?

    /**
     * Builds a new Game object, with the fleet confirmed.
     * This function will result in a new Game object, with the state changed to BATTLE.
     * From this point, it is not possible to place/move/rotate new ships.
     */
    abstract fun tryConfirmFleet(): Game?

    abstract fun tryRotateShip(position: Coordinate): Game?

    abstract fun tryRotateShip(position: String): Game?

    abstract fun isShip(it: Coordinate): Boolean

    abstract fun generateShips() : Game?
}

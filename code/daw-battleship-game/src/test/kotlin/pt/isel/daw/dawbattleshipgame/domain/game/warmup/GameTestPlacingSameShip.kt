package pt.isel.daw.dawbattleshipgame.domain.game.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.game.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate


class GameTestPlacingSameShip {
    private val gameConfig = getGameTestConfiguration()

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with same orientation, in different valid location`() {
        val game = Game.newGame(gameConfig)
        val gameResult1 = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.DESTROYER, "B9".toCoordinate()!!, Orientation.HORIZONTAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with different orientation, in different valid location`() {
        val game = Game.newGame(gameConfig)
        val gameResult1 = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.DESTROYER, "J1".toCoordinate()!!, Orientation.VERTICAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, in valid location`() {
        val game = Game.newGame(gameConfig)
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "A8".toCoordinate()!!, Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, apon previous one`() {
        val game = Game.newGame(gameConfig)
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, apon previous one, but different orientation`() {
        val game = Game.newGame(gameConfig)
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate()!!, Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate()!!, Orientation.VERTICAL)
        assertNull(gameResult3)
    }
}
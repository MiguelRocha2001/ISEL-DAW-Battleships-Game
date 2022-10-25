package pt.isel.daw.dawbattleshipgame.domain.warmup

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration


class GameTestPlacingSameShip {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with same orientation, in different valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.DESTROYER, "B9".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with different orientation, in different valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.DESTROYER, "J1".toCoordinate(), Orientation.VERTICAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, in valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "A8".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one`() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one, but different orientation`() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.VERTICAL)
        assertNull(gameResult3)
    }

}
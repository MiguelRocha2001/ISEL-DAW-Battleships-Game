package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
=======
>>>>>>> Stashed changes
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId


class GameTestPlacingSameShip {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with same orientation, in different valid location`() {
<<<<<<< Updated upstream
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult1 = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.DESTROYER, "B9".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.DESTROYER, "B9".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes
        assertNull(gameResult2)
    }

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with different orientation, in different valid location`() {
<<<<<<< Updated upstream
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult1 = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.DESTROYER, "J1".toCoordinate(), Orientation.VERTICAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.DESTROYER, "J1".toCoordinate(), Orientation.VERTICAL)
>>>>>>> Stashed changes
        assertNull(gameResult2)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, in valid location`() {
<<<<<<< Updated upstream
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "A8".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "A8".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one`() {
<<<<<<< Updated upstream
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one, but different orientation`() {
<<<<<<< Updated upstream
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult1 = game.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.VERTICAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult1 = game.logic.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.logic?.tryPlaceShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.logic?.tryPlaceShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.VERTICAL)
>>>>>>> Stashed changes
        assertNull(gameResult3)
    }

}
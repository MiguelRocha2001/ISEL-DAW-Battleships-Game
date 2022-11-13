package pt.isel.daw.dawbattleshipgame.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.*
import pt.isel.daw.dawbattleshipgame.utils.*

class GameServicesConfirmLayoutTests {
    private val configuration = getGameTestConfiguration1()

    @Test
    fun confirmFleet() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val userPair = createUserPair(transactionManager)
            val gameServices = GameServices(transactionManager)

            // Create Game
            val gameId = createGame(transactionManager, userPair.first, userPair.second, configuration)

            // apply some actions with player_1
            placeShip(gameServices, userPair.first, ShipType.BATTLESHIP, Coordinate(2, 3), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.SUBMARINE, Coordinate(5, 5), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.first, ShipType.DESTROYER, Coordinate(7, 7), Orientation.VERTICAL)
            placeShip(gameServices, userPair.second, ShipType.CRUISER, Coordinate(5, 9), Orientation.VERTICAL)
            placeShip(gameServices, userPair.first, ShipType.CARRIER, Coordinate(1, 5), Orientation.HORIZONTAL)
            placeShip(gameServices, userPair.second, ShipType.CARRIER, Coordinate(1, 1), Orientation.VERTICAL)


            var game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)

            gameServices.updateFleetState(userPair.first, true)
            game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.FLEET_SETUP, game.value.state)

            gameServices.updateFleetState(userPair.second, true)
            game = gameServices.getGame(gameId) as Either.Right
            assertEquals(GameState.BATTLE, game.value.state)
        }
    }
}
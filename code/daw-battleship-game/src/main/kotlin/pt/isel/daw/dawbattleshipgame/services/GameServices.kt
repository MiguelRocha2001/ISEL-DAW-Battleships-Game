package pt.isel.daw.dawbattleshipgame.services

import pt.isel.daw.dawbattleshipgame.data.GameConfigData
import pt.isel.daw.dawbattleshipgame.data.GameData
import pt.isel.daw.dawbattleshipgame.model.Board
import pt.isel.daw.dawbattleshipgame.model.Coordinate
import pt.isel.daw.dawbattleshipgame.model.Orientation
import pt.isel.daw.dawbattleshipgame.model.game.State
import pt.isel.daw.dawbattleshipgame.model.ship.ShipType

class GameServices(private val data: GameData, private val gameConfigData: GameConfigData) {

    private fun startGame() {
        val gameConfiguration = gameConfigData.configuration
        data.startNewGame(gameConfiguration)
    }

    private fun placeShip(ship: ShipType, position: Coordinate, orientation: Orientation) {
        data.placeShip(ship, position, orientation)
    }

    private fun confirmFleet() {
        data.confirmFleet()
    }

    private fun placeShot(c: Coordinate) {
        data.placeShot(c)
    }

    private fun getMyFleetLayout(): Board? {
        return data.myBoard
    }

    private fun getEnemyFleetLayout(): Board? {
        return data.opponentBoard
    }

    private fun getGameState(): State? {
        return data.getGameState()
    }

    private fun rotateShip(position: Coordinate) {
        data.rotateShip(position)
    }
}
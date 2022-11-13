package pt.isel.daw.dawbattleshipgame.services

import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.services.game.GameServices
import pt.isel.daw.dawbattleshipgame.services.game.PlaceShipsError

fun placeShip(
    services: GameServices,
    userId: Int,
    shipType: ShipType,
    coordinate: Coordinate,
    orientation: Orientation
): Either<PlaceShipsError, Unit> = services.placeShips(userId, listOf(Triple(shipType, coordinate, orientation)))
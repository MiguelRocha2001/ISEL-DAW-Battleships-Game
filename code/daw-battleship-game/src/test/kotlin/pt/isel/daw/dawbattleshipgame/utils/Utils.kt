package pt.isel.daw.dawbattleshipgame.utils

import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.http.model.game.CreateGameInputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.ShipTypeInputModel

fun getGameTestConfiguration() = Configuration(
    boardSize = 10,
    fleet = setOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.KRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun getCreateGameInputModel() = CreateGameInputModel(
    boardSize = 10,
    fleet = mapOf(
        ShipTypeInputModel.CARRIER to 5,
        ShipTypeInputModel.BATTLESHIP to 4,
        ShipTypeInputModel.CRUISER to 3,
        ShipTypeInputModel.SUBMARINE to 3,
        ShipTypeInputModel.DESTROYER to 2
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun generateGameId(): Int = (Math.random() * 100000).toInt()

fun generateToken(): String = (Math.random() * 100000).toString()

fun getRandomPassword(): String = "A" + (Math.random() * 100000).toString()
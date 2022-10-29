package pt.isel.daw.dawbattleshipgame.utils

import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration

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

fun generateGameId(): Int = (Math.random() * 100000).toInt()

fun generateToken(): String = (Math.random() * 100000).toString()

fun getRandomPassword(): String = "A" + (Math.random() * 100000).toString()
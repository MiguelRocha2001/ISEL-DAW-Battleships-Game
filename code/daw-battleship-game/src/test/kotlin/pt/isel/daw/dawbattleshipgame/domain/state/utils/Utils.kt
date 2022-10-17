package pt.isel.daw.dawbattleshipgame.domain.state.utils

import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

fun getGameTestConfiguration() = Configuration(
    boardSize = 10,
    fleet = setOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    nShotsPerRound = 10,
    roundTimeout = 10
)

fun generateGameId(): Int = (Math.random() * 100000).toInt()

fun generateToken(): String = (Math.random() * 100000).toString()
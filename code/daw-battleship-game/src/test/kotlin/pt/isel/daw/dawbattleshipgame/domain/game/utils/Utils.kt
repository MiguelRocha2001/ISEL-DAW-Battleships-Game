package pt.isel.daw.dawbattleshipgame.domain.game.utils

import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
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
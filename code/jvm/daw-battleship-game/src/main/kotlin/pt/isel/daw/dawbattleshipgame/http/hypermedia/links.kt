package pt.isel.daw.dawbattleshipgame.http.hypermedia

import pt.isel.daw.dawbattleshipgame.http.controllers.Rels
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris

fun homeLinks() = listOf(
    Uris.Server.home() to Rels.SELF,
    Uris.Server.serverInfo() to Rels.SERVER_INFO,
    Uris.Users.battleshipsStatistics() to Rels.BATTLESHIPS_STATISTICS,
    Uris.Users.createToken() to Rels.TOKEN,
    Uris.Users.register() to Rels.REGISTER,
)

fun gameByIdLinks(gameId: Int) = listOf(
    Uris.Games.gameById(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO,
)

fun placeShipLinks(gameId: Int) = listOf(
    Uris.Games.placeShip(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO,
)

fun moveShipLinks(gameId: Int) = listOf(
    Uris.Games.moveShip(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO,
)

fun rotateShipLinks(gameId: Int) = listOf(
    Uris.Games.rotateShip(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO,
)

fun placeShotLinks(gameId: Int) = listOf(
    Uris.Games.placeShot(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO,
)

fun confirmFleetLinks(gameId: Int) = listOf(
    Uris.Games.confirmFleet(gameId) to Rels.SELF,
    Uris.Games.gameInfo(gameId) to Rels.GAME_INFO
)
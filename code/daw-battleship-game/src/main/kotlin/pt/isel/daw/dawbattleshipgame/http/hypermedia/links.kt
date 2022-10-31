package pt.isel.daw.dawbattleshipgame.http.hypermedia

import pt.isel.daw.dawbattleshipgame.http.controllers.Rels
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris

fun homeLinks() = listOf(
    Uris.home() to Rels.SELF,
    Uris.serverInfo() to Rels.SERVER_INFO,
    Uris.battleshipsStatistics() to Rels.BATTLESHIPS_STATISTICS,
    Uris.createToken() to Rels.TOKEN,
    Uris.register() to Rels.REGISTER,
)

fun startGameLinks(gameId: Int?) = listOfNotNull(
    Uris.gameCreate() to Rels.SELF,
    if (gameId != null)
        Uris.gameById(gameId) to Rels.GAME_ID
    else
        Uris.currentGameId() to Rels.GAME_ID
)

fun gameByIdLinks(gameId: Int) = listOf(
    Uris.gameById(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO,
)

fun placeShipLinks(gameId: Int) = listOf(
    Uris.placeShip(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO,
)

fun moveShipLinks(gameId: Int) = listOf(
    Uris.moveShip(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO,
)

fun rotateShipLinks(gameId: Int) = listOf(
    Uris.rotateShip(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO,
)

fun placeShotLinks(gameId: Int) = listOf(
    Uris.placeShot(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO,
)

fun confirmFleetLinks(gameId: Int) = listOf(
    Uris.confirmFleet(gameId) to Rels.SELF,
    Uris.gameInfo(gameId) to Rels.GAME_INFO
)
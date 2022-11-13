package pt.isel.daw.dawbattleshipgame.http.hypermedia

import pt.isel.daw.dawbattleshipgame.http.controllers.Rels
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris

fun homeLinks() = listOf(
    Uris.Home.home() to Rels.SELF,
    Uris.Users.stats() to Rels.USERS_STATS,
    Uris.Home.serverInfo() to Rels.SERVER_INFO,
    Uris.Users.home() to Rels.USER_HOME
)

fun gameByIdLinks(gameId: Int) = listOf(
    Uris.Games.byId(gameId) to Rels.SELF,
    Uris.Games.byId(gameId) to Rels.GAME_INFO,
)

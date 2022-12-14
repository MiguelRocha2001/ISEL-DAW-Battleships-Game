package pt.isel.daw.dawbattleshipgame.http.hypermedia

import pt.isel.daw.dawbattleshipgame.http.controllers.Rels
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameIdOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel

fun SirenBuilderScope<HomeOutputModel>.homeLinks() = links(
    listOf(
        Uris.Home.home() to Rels.SELF,
        Uris.Users.stats() to Rels.USERS_STATS,
        Uris.Home.serverInfo() to Rels.SERVER_INFO,
        Uris.Users.home() to Rels.USER_HOME
    )
)

fun SirenBuilderScope<GameIdOutputModel>.gameByIdLinks(gameId: Int) = links(
    listOf(
        Uris.Games.byId(gameId) to Rels.SELF,
        Uris.Games.My.current() to Rels.GAME,
    )
)

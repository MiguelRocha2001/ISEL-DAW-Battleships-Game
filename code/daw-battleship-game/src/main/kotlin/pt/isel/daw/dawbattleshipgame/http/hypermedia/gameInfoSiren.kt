package pt.isel.daw.dawbattleshipgame.http.hypermedia

import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris

fun gameInfoActions(gameId: Int) = listOf(
    LinkOutputModel(
        relation = LinkRelation.SELF,
        targetUri = Uris.gameInfo(gameId)
    )
)

fun startGameLinks(userId: Int) = listOf(
    LinkOutputModel(
        relation = LinkRelation.SELF,
        targetUri = Uris.gameCreate()
    ),
    LinkOutputModel(
        relation = LinkRelation.GAME_ID,
        targetUri = Uris.gameById(userId)
    )
)

fun gameInfoLink(gameId: Int) = LinkOutputModel(
    relation = LinkRelation.GAME_ID,
    targetUri = Uris.gameById(gameId)
)
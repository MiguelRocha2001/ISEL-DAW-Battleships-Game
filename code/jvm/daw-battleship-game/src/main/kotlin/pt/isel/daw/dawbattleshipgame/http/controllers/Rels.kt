package pt.isel.daw.dawbattleshipgame.http.controllers

import pt.isel.daw.dawbattleshipgame.http.infra.LinkRelation

object Rels {

    val SELF = LinkRelation("self")

    val HOME = LinkRelation(
        "https://github.com/isel-leic-daw/s2223i-51d-51n-public/tree/main/code/tic-tac-tow-service/docs/" +
                "rels/home"
    )

    // ------------------- SERVER -------------------
    val SERVER_INFO = LinkRelation("server-info")

    val BATTLESHIPS_STATISTICS = LinkRelation("battleships-statistics")

    // ------------------- USERS -------------------
    val TOKEN = LinkRelation("token")
    val USER_HOME = LinkRelation("user-home")
    val REGISTER = LinkRelation("register")
    val USERS_STATS = LinkRelation("user-stats")

    // ------------------- GAMES -------------------
    val GAME_ID = LinkRelation("game-id")
    val GAME_CREATE = LinkRelation("game-create")
    val GAME_INFO = LinkRelation("game-info")
}
package pt.isel.daw.dawbattleshipgame.http.controllers

import pt.isel.daw.tictactow.infra.LinkRelation

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
    val LOGIN = LinkRelation("login")

    val LOGOUT = LinkRelation("logout")

    val REGISTER = LinkRelation("register")

    // ------------------- GAMES -------------------
    val GAME_ID = LinkRelation("game-id")
    val GAME_CREATE = LinkRelation("game-create")
    val GAME_INFO = LinkRelation("game-info")
}
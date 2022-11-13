package pt.isel.daw.dawbattleshipgame.http.controllers

import pt.isel.daw.dawbattleshipgame.http.infra.LinkRelation

object Rels {

    val SELF = LinkRelation("self")

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
    val GAME = LinkRelation("game")
    val GAME_INFO = LinkRelation("game-info")
}
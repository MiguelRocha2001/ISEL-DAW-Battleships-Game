package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    const val HOME = "/"
    const val USER_HOME = "/me"
    const val GAME_BY_ID = "/games/{id}"

    const val USERS_CREATE = "/users"
    const val USERS_TOKEN = "/users/token"
    const val USERS_BY_ID = "/users/{id}"

    const val GAMES_CREATE = "/games"
    const val GAMES_GET_GAME_ID = "/games/current"
    const val GAMES_PLACE_SHIP = "/games/{id}/place-ship"
    const val GAMES_MOVE_SHIP = "/games/{id}/move-ship"
    const val GAMES_ROTATE_SHIP = "/games/{id}/rotate-ship"
    const val GAMES_PLACE_SHOT = "/games/{id}/place-shot"
    const val GAMES_CONFIRM_FLEET = "/games/{id}/confirm-fleet"
    const val GAMES_GET_MY_FLEET = "/games/{id}/my-fleet"
    const val GAMES_GET_OPPONENT_FLEET = "/games/{id}/opponent-fleet"
    const val GAMES_STATE = "/games/{id}/state"
    const val GAMES_DELETE = "/games/{id}"

    const val SERVER_INFO = "/server-info"
    const val USERS_STATS = "/user/statistics"

    fun home(): URI = URI(HOME)
    fun info(): URI = URI(SERVER_INFO)
    fun userHome(): URI = URI(USER_HOME)

    // ------------------ USERS ------------------
    fun userCreate() = URI(USERS_CREATE)
    fun userById(id: Int) = UriTemplate(USERS_BY_ID).expand(id)
    fun createToken(): URI = URI(USERS_TOKEN)
    fun logout(): URI = URI(USERS_TOKEN)
    fun register(): URI = URI(USERS_CREATE)
    fun usersStats(): URI = URI(USERS_STATS)

    // ------------------ SERVER ------------------
    fun serverInfo(): URI = URI(SERVER_INFO)
    fun battleshipsStatistics(): URI = URI(USERS_STATS)

    // ------------------ GAMES ------------------
    fun gameCreate() = URI(GAMES_CREATE)
    fun gameById(gameId: Int) = UriTemplate(GAME_BY_ID).expand(gameId)
    fun gameInfo(gameId: Int) = UriTemplate(GAME_BY_ID).expand(gameId)
    fun currentGameId(): URI = URI(GAMES_GET_GAME_ID)

    fun placeShip(gameId: Int) = UriTemplate(GAMES_PLACE_SHIP).expand(gameId)
    fun moveShip(gameId: Int) = UriTemplate(GAMES_MOVE_SHIP).expand(gameId)
    fun rotateShip(gameId: Int) = UriTemplate(GAMES_ROTATE_SHIP).expand(gameId)
    fun placeShot(gameId: Int) = UriTemplate(GAMES_PLACE_SHOT).expand(gameId)
    fun confirmFleet(gameId: Int) = UriTemplate(GAMES_CONFIRM_FLEET).expand(gameId)
}
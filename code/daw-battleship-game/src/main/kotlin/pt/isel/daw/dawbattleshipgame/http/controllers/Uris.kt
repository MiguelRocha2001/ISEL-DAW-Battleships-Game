package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.util.UriTemplate
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import java.net.URI

object Uris {

    const val HOME = "/"
    const val USER_HOME = "/me"
    const val GAME_BY_ID = "/games/{gid}"

    const val USERS_CREATE = "/users"
    const val USERS_TOKEN = "/users/token"
    const val USERS_GET_BY_ID = "/users/{id}"

    const val SERVER_INFO = "/server-info"
    const val BATTLESHIPS_STATISTICS = "/battleships-statistics"

    fun home(): URI = URI(HOME)
    fun userHome(): URI = URI(USER_HOME)
    fun gameById(game: Game) = UriTemplate(GAME_BY_ID).expand(game.gameId)

    fun userById(id: String) = UriTemplate(USERS_GET_BY_ID).expand(id)

    fun serverInfo(): URI = URI(SERVER_INFO)
    fun battleshipsStatistics(): URI = URI(BATTLESHIPS_STATISTICS)
}
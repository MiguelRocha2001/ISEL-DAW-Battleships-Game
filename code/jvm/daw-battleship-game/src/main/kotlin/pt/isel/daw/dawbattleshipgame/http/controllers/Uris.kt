package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    object Server {
        const val SERVER_INFO = "/info"
        const val HOME = "/"

        fun home(): URI = URI(HOME)
        fun info(): URI = URI(SERVER_INFO)
        fun serverInfo(): URI = URI(SERVER_INFO)
    }

    object Users {
        const val USERS_CREATE = "/users"
        const val USERS_TOKEN = "/users/token"
        const val USERS_BY_ID = "/users/{id}"
        const val USERS_STATS = "/users/all/statistics"
        const val USER_HOME = "/me"

        fun userCreate() = URI(USERS_CREATE)
        fun userById(id: Int) = UriTemplate(USERS_BY_ID).expand(id)
        fun createToken(): URI = URI(USERS_TOKEN)
        fun userHome(): URI = URI(USER_HOME)
        fun logout(): URI = URI(USERS_TOKEN)
        fun register(): URI = URI(USERS_CREATE)
        fun usersStats(): URI = URI(USERS_STATS)
        fun battleshipsStatistics(): URI = URI(USERS_STATS)
    }

    object Games {
        const val ALL = "/games"

        object Ships {
            const val ALL = Games.ALL + "/ships"
            const val BY_ID = "$ALL/ships/{id}"
            fun all(): URI = URI(ALL)
            fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
        }
        object Shots {
            const val ALL = Games.ALL + "/shots"
            const val BY_ID = "$ALL/shots/{id}"
            fun all(): URI = URI(ALL)
            fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
        }

        object My {
            const val ALL = "/my/games"
            const val BY_ID = "/my/games/{id}"
            const val CURRENT = "$ALL/current"

            fun all(): URI = URI(ALL)
            fun byId(id: Int) = UriTemplate(BY_ID).expand(id)

            object Current {
                object My {
                    object Ships {
                        const val ALL = "$CURRENT/ships"
                        const val BY_ID = "$ALL/{id}"
                        fun all(): URI = URI(ALL)
                        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
                    }

                    object Shots {
                        const val ALL = "$CURRENT/shots"
                        const val BY_ID = "$ALL/{id}"
                        fun all(): URI = URI(ALL)
                        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
                    }
                }
                object Opponent {
                    object Ships {
                        const val ALL = "$CURRENT/opponent/ships"
                        const val BY_ID = "$ALL/{id}"
                        fun all(): URI = URI(ALL)
                        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
                    }

                    object Shots {
                        const val ALL = "$CURRENT/opponent/shots"
                        const val BY_ID = "$ALL/{id}"
                        fun all(): URI = URI(ALL)
                        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
                    }
                }
            }
        }
    }
}
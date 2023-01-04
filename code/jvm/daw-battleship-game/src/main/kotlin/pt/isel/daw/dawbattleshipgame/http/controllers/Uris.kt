package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {

    object Home {
        const val SERVER_INFO = "/info"
        const val HOME = "/"

        fun home(): URI = URI(HOME)
        fun info(): URI = URI(SERVER_INFO)
        fun serverInfo(): URI = URI(SERVER_INFO)
    }

    object Users {
        const val ALL = "/users"
        const val TOKEN = "/users/token"
        const val BY_ID1 = "/users/{id}"
        private const val BY_ID2 = "/users/:id"
        const val STATS = "/users/all/statistics"
        const val HOME = "/me"

        fun all(): URI = URI(ALL)
        fun create() = URI(ALL)
        fun byId() = URI(BY_ID2)
        fun byId(id: Int) = UriTemplate(BY_ID1).expand(id)
        fun createToken(): URI = URI(TOKEN)
        fun home(): URI = URI(HOME)
        fun logout(): URI = URI(TOKEN)
        fun register(): URI = URI(ALL)
        fun stats(): URI = URI(STATS)
        fun battleshipsStatistics(): URI = URI(STATS)
    }

    object Games {
        const val ALL = "/games"
        const val BY_ID = "/games/:id"

        fun all(): URI = URI(ALL)
        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)

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
            const val CURRENT_ID = "$CURRENT/id"

            fun all(): URI = URI(ALL)
            fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
            fun current(): URI = URI(CURRENT)

            object Current {
                object My {
                    object Ships {
                        const val ALL = "$CURRENT/my/ships"
                        const val BY_ID = "$ALL/{id}"
                        fun all(): URI = URI(ALL)
                        fun byId(id: Int) = UriTemplate(BY_ID).expand(id)
                    }

                    object Shots {
                        const val ALL = "$CURRENT/my/shots"
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
package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import java.net.URI

val homeSirenActions = listOf(
    SirenAction(
        name = "create-user",
        title = "Create User",
        method = HttpMethod.POST,
        href = URI(Uris.USERS_CREATE),
        type = "application/json",
        fields = listOf(
            SirenAction.Field(
                name = "username",
                type = "text",
                title = "Username"
            ),
            SirenAction.Field(
                name = "password",
                type = "hidden",
                title = "Password"
            )
        )
    ),
    SirenAction(
        name = "login",
        title = "Login",
        method = HttpMethod.GET,
        href = URI(Uris.USERS_TOKEN),
        type = "application/json",
        fields = listOf(
            SirenAction.Field(
                name = "username",
                type = "text",
                title = "Username"
            ),
            SirenAction.Field(
                name = "password",
                type = "hidden",
                title = "Password"
            )
        )
    )
)

val homeSirenLinks = listOf(
    LinkOutputModel(
        Uris.home(),
        LinkRelation.SELF
    ),
    LinkOutputModel(
        Uris.serverInfo(),
        LinkRelation.SERVER_INFO
    ),
    LinkOutputModel(
        Uris.battleshipsStatistics(),
        LinkRelation.BATTLESHIPS_STATISTICS
    ),
    LinkOutputModel(
        Uris.login(),
        LinkRelation.LOGIN
    ),
    LinkOutputModel(
        Uris.register(),
        LinkRelation.REGISTER
    ),
)
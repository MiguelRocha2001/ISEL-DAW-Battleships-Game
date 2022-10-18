package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.hypermedia.LinkOutputModel
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import java.net.URI

@RestController
class HomeController {

    @GetMapping(Uris.HOME)
    fun getHome() = HomeOutputModel(
        sirenActions = listOf(
            SirenAction(
                name = "create-user",
                title = "Create User",
                method = HttpMethod.POST,
                href = URI(Uris.USERS_CREATE),
                type = MediaType.APPLICATION_JSON,
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
                type = MediaType.APPLICATION_JSON,
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
        ),
        links = listOf(
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
    )
}
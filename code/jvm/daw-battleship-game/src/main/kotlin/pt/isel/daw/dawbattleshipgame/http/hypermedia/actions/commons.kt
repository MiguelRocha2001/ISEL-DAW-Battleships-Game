package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import java.net.URI

fun createTokenSirenAction(sirenBuilderScope: SirenBuilderScope<*>) =
    sirenBuilderScope.action(
        name = "create-token",
        href = URI(Uris.Users.TOKEN),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.textField("username")
        this.textField("password")
    }
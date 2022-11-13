package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import java.net.URI

fun buildHomeActions(sirenBuilderScope: SirenBuilderScope<HomeOutputModel>) {
    // Create User
    sirenBuilderScope.action(
        name = "create-user",
        href = URI(Uris.Users.CREATE),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.textField("username")
        this.textField("password")
    }
    createTokenSirenAction(sirenBuilderScope)
}
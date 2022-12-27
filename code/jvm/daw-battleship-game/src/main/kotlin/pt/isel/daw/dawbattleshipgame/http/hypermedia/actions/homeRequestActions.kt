package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import java.net.URI

fun createUserSirenAction(sirenBuilderScope: SirenBuilderScope<HomeOutputModel>) {
    // Create User
    sirenBuilderScope.action(
        name = "create-user",
        href = URI(Uris.Users.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField("username")
        this.textField("password")
    }
}
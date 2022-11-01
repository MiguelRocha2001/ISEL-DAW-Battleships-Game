package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.user.TokenOutputModel
import java.net.URI

fun buildTokenRequestActions(sirenBuilderScope: SirenBuilderScope<TokenOutputModel>) {
    sirenBuilderScope.action(
        name = "start-game",
        href = URI(Uris.GAMES_CREATE),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        numberField("boardSize")
        numberField("nShotsPerRound")
        numberField("roundTimeout")
        // TODO(): Add fleet
    }
}
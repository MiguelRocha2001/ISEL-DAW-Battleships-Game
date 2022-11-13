package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.user.TokenOutputModel
import java.net.URI

fun buildStartGameAction(sirenBuilderScope: SirenBuilderScope<*>) {
    sirenBuilderScope.action(
        name = "start-game",
        href = URI(Uris.Games.ALL),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        numberField("boardSize")
        numberField("nShotsPerRound")
        numberField("roundTimeout")
        // TODO(): Add fleet
    }
}
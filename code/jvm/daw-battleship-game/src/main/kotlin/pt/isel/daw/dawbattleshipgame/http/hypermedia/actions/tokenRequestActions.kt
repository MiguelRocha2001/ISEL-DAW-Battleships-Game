package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import java.net.URI

fun buildStartGameAction(sirenBuilderScope: SirenBuilderScope<*>) {
    sirenBuilderScope.action(
        name = "create-game",
        href = URI(Uris.Games.My.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        numberField("boardSize")
        numberField("nShotsPerRound")
        numberField("roundTimeout")
        // TODO(): Add fleet
    }
}
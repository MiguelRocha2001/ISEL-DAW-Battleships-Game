package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameOutputModel
import java.net.URI

fun buildBattleActions(sirenBuilderScope: SirenBuilderScope<GameOutputModel>) {
    sirenBuilderScope.action(
        name = "place-shot",
        href = URI(Uris.Games.My.Current.My.Shots.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.numberField("row")
        this.numberField("column")
    }
}
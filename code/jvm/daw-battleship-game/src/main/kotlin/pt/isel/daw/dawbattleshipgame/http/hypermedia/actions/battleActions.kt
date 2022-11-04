package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameOutputModel
import java.net.URI

fun buildBattleActions(sirenBuilderScope: SirenBuilderScope<GameOutputModel>) {
    sirenBuilderScope.action(
        name = "place-shot",
        href = URI(Uris.GAMES_PLACE_SHOT.replace("{id}", ":id")),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.numberField("row")
        this.numberField("column")
    }
}
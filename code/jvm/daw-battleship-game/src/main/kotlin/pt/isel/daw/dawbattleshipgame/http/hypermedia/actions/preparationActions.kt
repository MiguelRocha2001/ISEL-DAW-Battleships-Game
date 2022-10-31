package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameOutputModel
import java.net.URI

fun buildPreparationActions(sirenBuilderScope: SirenBuilderScope<GameOutputModel>) {
    // Place Ship
    sirenBuilderScope.action(
        name = "place-ship",
        href = URI(Uris.GAMES_PLACE_SHIP),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.numberField("row")
        this.numberField("column")
    }

    // Move Ship
    sirenBuilderScope.action(
        name = "move-ship",
        href = URI(Uris.GAMES_MOVE_SHIP),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.numberField("origin_row")
        this.numberField("origin_column")
        this.numberField("destination_row")
        this.numberField("destination_column")
    }

    // Rotate Ship
    sirenBuilderScope.action(
        name = "rotate-ship",
        href = URI(Uris.GAMES_ROTATE_SHIP),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        this.numberField("row")
        this.numberField("column")
    }

    // Confirm Fleet
    sirenBuilderScope.action(
        name = "confirm-fleet",
        href = URI(Uris.GAMES_CONFIRM_FLEET),
        method = HttpMethod.POST,
        type = "application/json"
    ) {
        // DO NOTHING
    }
}


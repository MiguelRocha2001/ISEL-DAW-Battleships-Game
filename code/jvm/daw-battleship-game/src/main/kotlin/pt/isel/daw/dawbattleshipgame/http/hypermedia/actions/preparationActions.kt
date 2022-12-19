package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameOutputModel
import java.net.URI

fun buildPreparationActions(sirenBuilderScope: SirenBuilderScope<GameOutputModel>) {
    // Place Ship
    sirenBuilderScope.action(
        name = "place-ships",
        href = URI(Uris.Games.My.Current.My.Ships.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.textField(name = "operation", value = "place-ships")
        this.arrayField(
            name = "ships",
            block = {
                this.textField("shipType")
                this.objectField("position") {
                    this.textField("x")
                    this.textField("y")
                }
                this.textField("orientation")
            }
        )
        this.booleanField("fleetConfirmed")
    }

    // Move Ship
    sirenBuilderScope.action(
        name = "move-ship",
        href = URI(Uris.Games.My.Current.My.Ships.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.numberField("origin_row")
        this.numberField("origin_column")
        this.numberField("destination_row")
        this.numberField("destination_column")
    }

    // Rotate Ship
    sirenBuilderScope.action(
        name = "rotate-ship",
        href = URI(Uris.Games.My.Current.My.Ships.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.numberField("row")
        this.numberField("column")
    }

    // Confirm Fleet
    sirenBuilderScope.action(
        name = "confirm-fleet",
        href = URI(Uris.Games.My.Current.My.Ships.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        // DO NOTHING
    }
}


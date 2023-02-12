package pt.isel.daw.dawbattleshipgame.http.hypermedia.actions

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenBuilderScope
import pt.isel.daw.dawbattleshipgame.http.model.game.GameOutputModel
import java.net.URI

fun preparationActions(sirenBuilderScope: SirenBuilderScope<*>) {
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

    // Confirm Fleet
    sirenBuilderScope.action(
        name = "confirm-fleet",
        href = URI(Uris.Games.My.Current.My.Ships.ALL),
        method = HttpMethod.PUT,
        type = MediaType.APPLICATION_JSON
    ) {
        this.booleanField("fleetConfirmed")
    }
}

fun battleActions(sirenBuilderScope: SirenBuilderScope<*>) {
    sirenBuilderScope.action(
        name = "place-shots",
        href = URI(Uris.Games.My.Current.My.Shots.ALL),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {
        this.arrayField(
            name = "shots",
            block = {
                this.numberField("row")
                this.numberField("column")
            }
        )
    }
}

fun quitGameAction(sirenBuilderScope: SirenBuilderScope<*>) {
    sirenBuilderScope.action(
        name = "quit-game",
        href = URI(Uris.Games.BY_ID2),
        method = HttpMethod.POST,
        type = MediaType.APPLICATION_JSON
    ) {}
}

fun quitGameQueueAction(sirenBuilderScope: SirenBuilderScope<*>) {
    sirenBuilderScope.action(
        name = "quit-game-queue",
        href = URI(Uris.Games.Queue.ME),
        method = HttpMethod.DELETE,
        type = MediaType.APPLICATION_JSON
    ) {}
}
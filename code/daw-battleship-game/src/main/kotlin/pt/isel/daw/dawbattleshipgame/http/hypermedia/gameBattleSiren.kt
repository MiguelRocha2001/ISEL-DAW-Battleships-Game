package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import java.net.URI

val battleSirenActions = listOf(
    SirenAction(
        name = "place-shot",
        title = "Place Shot",
        method = HttpMethod.POST,
        href = URI(Uris.GAMES_PLACE_SHOT),
        type = "application/json",
        fields = listOf(
            SirenAction.Field(
                name = "row",
                type = "number",
            ),
            SirenAction.Field(
                name = "column",
                type = "number",
            ),
        )
    )
)
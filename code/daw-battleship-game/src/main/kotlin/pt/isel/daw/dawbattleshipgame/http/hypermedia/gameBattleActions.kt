package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import java.net.URI

fun getGameBattleActions(): List<SirenAction> {
    return listOf(
        SirenAction(
            name = "place-shot",
            title = "Place Shot",
            method = HttpMethod.POST,
            href = URI(Uris.GAMES_PLACE_SHOT),
            type = MediaType.APPLICATION_JSON,
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
}
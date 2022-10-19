package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import java.net.URI

fun getGamePreparationActions(): List<SirenAction> {
    return listOf(
        SirenAction(
            name = "place-ship",
            title = "Place Ship",
            method = HttpMethod.POST,
            href = URI(Uris.GAMES_PLACE_SHIP),
            type = MediaType.APPLICATION_JSON,
            fields = listOf(
                SirenAction.Field(
                    name = "shipType",
                    type = "text",
                ),
                SirenAction.Field(
                    name = "position",
                    fields = listOf(
                        SirenAction.Field(
                            name = "row",
                            type = "number"
                        ),
                        SirenAction.Field(
                            name = "column",
                            type = "number"
                        ),
                    )
                ),
                SirenAction.Field(
                    name = "orientation",
                    type = "text",
                )
            )
        ),
        SirenAction(
            name = "move-ship",
            title = "Move Ship",
            method = HttpMethod.POST,
            href = URI(Uris.GAMES_MOVE_SHIP),
            type = MediaType.APPLICATION_JSON,
            fields = listOf(
                SirenAction.Field(
                    name = "origin",
                    fields = listOf(
                        SirenAction.Field(
                            name = "row",
                            type = "number"
                        ),
                        SirenAction.Field(
                            name = "column",
                            type = "number"
                        ),
                    )
                ),
                SirenAction.Field(
                    name = "destination",
                    fields = listOf(
                        SirenAction.Field(
                            name = "row",
                            type = "number"
                        ),
                        SirenAction.Field(
                            name = "column",
                            type = "number"
                        ),
                    )
                ),
            )
        ),
        SirenAction(
            name = "rotate-ship",
            title = "Rotate Ship",
            method = HttpMethod.POST,
            href = URI(Uris.GAMES_ROTATE_SHIP),
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
        ),
        SirenAction(
            name = "confirm-fleet",
            title = "Confirm Fleet",
            method = HttpMethod.POST,
            href = URI(Uris.GAMES_ROTATE_SHIP),
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
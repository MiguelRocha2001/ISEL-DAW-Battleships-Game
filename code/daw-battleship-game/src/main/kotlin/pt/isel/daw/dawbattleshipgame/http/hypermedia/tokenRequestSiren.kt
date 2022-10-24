package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import java.net.URI

val tokenRequestSirenActions = listOf(
    SirenAction(
        name = "start-game",
        title = "Start Game",
        method = HttpMethod.POST,
        href = URI(Uris.GAMES_CREATE),
        type = "application/json",
        fields = listOf(
            SirenAction.Field(
                name = "configuration",
                fields = listOf(
                    SirenAction.Field(
                        name = "boardSize",
                        type = "number",
                    ),
                    SirenAction.Field(
                        name = "nShotsPerRound",
                        type = "number",
                    ),
                    SirenAction.Field(
                        name = "roundTimeout",
                        type = "number",
                    ),
                    // TODO(): Add fleet
                )
            )
        )
    )
)
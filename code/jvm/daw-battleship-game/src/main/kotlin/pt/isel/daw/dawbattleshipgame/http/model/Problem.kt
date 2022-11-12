package pt.isel.daw.dawbattleshipgame.http.model

import org.springframework.http.ResponseEntity
import java.net.URI


/**
 * {
"type": "https://example.com/probs/out-of-credit",
"title": "You do not have enough credit.",
"detail": "Your current balance is 30, but that costs 50.",
"instance": "/account/12345/msgs/abc",
"balance": 30,
"accounts": ["/account/12345",
"/account/67890"]
}
 */

data class Problem(
        val type: URI,
        val title: String? = null,
        val detail: String? = null,
    ) {

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)

        val userAlreadyExists = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-exists"),
            "User already exists",
            "Try another name"
        )
        val insecurePassword = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/insecure-password"),
            "Insecure Password",
            "Password needs at least 4 characters including one uppercase letter"
        )

        val userOrPasswordAreInvalid = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-or-password-are-invalid"),
            "Invalid credentials",
           "Invalid name or password"
        )

        val userAlreadyInQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-queue"),
                "User already in a queue",
                "User is already in queue, unable to perform operation"
        )

        val userAlreadyInGame = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game"),
             "User already in game",
             "User is already in a game, unable to perform operation"
        )

        val userInGameQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-in-game-queue"),
            "User in game queue",
            "User is still in game queue"
        )

        val gameNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/game-not-found"),
            "Game not found",
            "Unable to find such game"
        )

        val actionNotPermitted = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            "Action not permitted",
            "Cannot perform such action"
        )

        val invalidMove = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            "Invalid move",
            "Invalid move"
        )

        val userNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            "Invalid user id",
            "User not found"
        )
    }
}
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

class Problem(
    type: URI,
    title: String? = null,
    detail: String? = null,
    ) {
    val type = type.toASCIIString()

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)

        val userAlreadyExists = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-exists"),
            title = "User already exists",
            detail = "Try another name"
        )
        val insecurePassword = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/insecure-password"),
            title = "Insecure Password",
            detail = "Password needs at least 4 characters including one uppercase letter"
        )

        val userOrPasswordAreInvalid = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-or-password-are-invalid"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val userAlreadyInQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-queue"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val userAlreadyInGame = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val userInGameQueue = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-in-game-queue"),
            title = "User in game queue",
            detail = "User is still in game queue"
        )

        val gameNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/game-not-found"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val actionNotPermitted = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val invalidMove = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/action-not-permitted"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val userNotFound = Problem(
            URI("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-not-found"),
            title = "Invalid user id",
            detail = "User not found"
        )
    }
}
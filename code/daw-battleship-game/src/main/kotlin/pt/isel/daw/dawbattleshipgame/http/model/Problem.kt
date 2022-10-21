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
        const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(problem)

        val userAlreadyExists = Problem(
            URI(
                "https://github.com/isel-leic-daw/s2223i-51d-51n-public/tree/main/code/tic-tac-tow-service/" +
                        "docs/problems/user-or-password-are-invalid"
            )
            title = "User already exists",
            detail = "Try another name"
        )
        val insecurePassword = Problem(
            URI("TODO"),
            title = "Insecure Password",
            detail = "Password needs at least 4 characters including one uppercase letter"
        )

        val userOrPasswordAreInvalid = Problem(
            URI("TODO"),
            title = "Invalid credentials",
            detail = "Invalid name or password"
        )

        val toBeChanged = Problem(
            URI(
            "TODO"
            )
        )
    }
}
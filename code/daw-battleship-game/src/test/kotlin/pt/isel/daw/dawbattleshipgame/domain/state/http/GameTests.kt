package pt.isel.daw.dawbattleshipgame.domain.state.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.http.model.game.GameIdOutputModel
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    private fun createUser(): String {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = "changeit"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("location") {
                assertTrue(it.startsWith("/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(UserTests.TokenResponse::class.java)
            .returnResult()
            .responseBody!!
        return result.token
    }

    @Test
    fun `can create an game`() {
        val configuration = getGameTestConfiguration()
        val player1Token = createUser()
        val player2Token = createUser()

        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(
                mapOf("configuration" to configuration)
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isOk

        // player 2 should be able to create a game
        client.post().uri("/games")
            .bodyValue(
                mapOf("configuration" to configuration)
            )
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isOk

        // player 1 should be able to get the game
        val gameId1 = client.get().uri("/games/current")
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isOk
            .expectBody(GameIdOutputModel::class.java)
            .returnResult()
            .responseBody?.id

        // player 2 should be able to get the game
        val gameId2 = client.get().uri("/games/current")
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isOk
            .expectBody(GameIdOutputModel::class.java)
            .returnResult()
            .responseBody?.id

        assertEquals(gameId1, gameId2)
    }

}
package pt.isel.daw.dawbattleshipgame.http

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    @TestConfiguration
    class GameTestConfiguration {

    }

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    /**
     * Creates a User and returns the token.
     * Also, asserts if the behavior is correct.
     */
    private fun createUser(): String {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        // when: creating a user
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
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No response body")

        return (result.properties as LinkedHashMap<String, String>)["token"] ?: fail("No token")
}

    /**
     * Creates two users and starts a game with them.
     * Also, asserts if the behavior is correct.
     * @return the game id along with the two tokens
     */
    private fun createGame(): Pair<Int, Pair<String, String>> {
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
        val gameId1Siren = client.get().uri("/games/current")
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("Game id is null")

        // assertEquals("Game", gameId1Siren.)
        val gameId1 = (gameId1Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: fail("Game id is null")

        // player 2 should be able to get the game
        val gameId2Siren = client.get().uri("/games/current")
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isOk
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("Game id is null")

        assertNotNull(gameId1Siren)
        val gameId2 = (gameId2Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: fail("Game id is null")

        assertEquals(gameId1, gameId2)

        return gameId1 to (player1Token to player2Token)
    }

    @Test
    fun `can create an game`() {
        createGame()
    }

    @Test
    fun `can place ship`() {
        val (gameId, tokens) = createGame()
        val player1Token = tokens.first

        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.post().uri("/games/{id}/place-ship", gameId)
            .bodyValue(
                mapOf(
                    "shipType" to "CARRIER",
                    "position" to mapOf(
                        "row" to 1,
                        "column" to 1
                    ),
                    "orientation" to "HORIZONTAL"
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isOk
    }
}
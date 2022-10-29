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
    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration {

    }

    /**
     * Creates a User and returns the token.
     * Also, asserts if the behavior is correct.
     */
    private fun createUser(): String {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // a random user
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
    private fun createGame(client: WebTestClient): Pair<Int, Pair<String, String>> {
        val configuration = getGameTestConfiguration()
        val player1Token = createUser()
        val player2Token = createUser()

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(
                "{\n" +
                        "    \"boardSize\": 10,\n" +
                        "    \"fleet\": {\n" +
                        "        \"CARRIER\": 5,\n" +
                        "        \"BATTLESHIP\": 4,\n" +
                        "        \"CRUISER\": 3,\n" +
                        "        \"SUBMARINE\": 3,\n" +
                        "        \"DESTROYER\": 2\n" +
                        "    },\n" +
                        "    \"nShotsPerRound\": 10,\n" +
                        "    \"roundTimeout\": 10\n" +
                        "}"
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isOk

        // player 2 should be able to create a game
        client.post().uri("/games")
            .bodyValue(
                "{\n" +
                        "    \"boardSize\": 10,\n" +
                        "    \"fleet\": {\n" +
                        "        \"CARRIER\": 5,\n" +
                        "        \"BATTLESHIP\": 4,\n" +
                        "        \"CRUISER\": 3,\n" +
                        "        \"SUBMARINE\": 3,\n" +
                        "        \"DESTROYER\": 2\n" +
                        "    },\n" +
                        "    \"nShotsPerRound\": 10,\n" +
                        "    \"roundTimeout\": 10\n" +
                        "}"
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
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        createGame(client)
    }

    @Test
    fun `single user tries to create two games in a row`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val player1Token = createGame(client).second.first
        val gameConfig = getGameTestConfiguration()

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(
                mapOf("configuration" to gameConfig)
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type").isEqualTo(
                "https://github.com/isel-leic-daw/s2223i-51d-51n-public/tree/main/code/" +
                        "tic-tac-tow-service/docs/problems/user-already-exists"
            )
    }

    @Test
    fun `can place ship`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val (gameId, tokens) = createGame(client)
        val player1Token = tokens.first

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
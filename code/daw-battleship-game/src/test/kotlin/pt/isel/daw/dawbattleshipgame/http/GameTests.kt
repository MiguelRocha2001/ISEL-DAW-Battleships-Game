package pt.isel.daw.dawbattleshipgame.http

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.utils.getCreateGameInputModel
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
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
    private fun createUser(): Pair<Int, String> {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        // when: creating a user
        // then: the response is a 201 with a proper Location header
        val siren = client.post().uri("/users")
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
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("Game id is null")

        assertNotNull(siren)
        val userId = (siren.properties as java.util.LinkedHashMap<String, *>)["userId"] as? Int ?: fail("Game id is null")

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

        val token = (result.properties as LinkedHashMap<String, String>)["token"] ?: fail("No token")
        return userId to token
}

    private data class GameInfo(val gameId: Int, val player1Id : Int, val player1Token: String, val player2Id : Int, val player2Token: String)

    /**
     * Creates two users and starts a game with them.
     * Also, asserts if the behavior is correct.
     * @return the game id along with the two tokens
     */
    private fun createGame(client: WebTestClient): GameInfo {
        val gameConfig = getCreateGameInputModel()

        val player1 = createUser()
        val player1Id = player1.first
        val player1Token = player1.second

        val player2 = createUser()
        val player2Id = player2.first
        val player2Token = player2.second

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isAccepted

        // player 2 should be able to create a game
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isCreated

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

        return GameInfo(gameId1, player1Id, player1Token, player2Id, player2Token)
    }

    @Test
    fun `can create an game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    private fun deleteGame(client: WebTestClient, gameId: Int) {
        client.delete().uri("/games/$gameId")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `user starts game correctly and then tries to start another one`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token
        val player2Token = gameInfo.player2Token

        val gameConfig = getCreateGameInputModel()

        // player 1 tries to create another game
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game")

        // player 2 tries to create another game
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game")

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `user requests to start game two times `() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val user = createUser()
        val userId = user.first
        val userToken = user.second
        val gameConfig = getCreateGameInputModel()

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isAccepted

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri("/games")
            .bodyValue(gameConfig)
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-queue")

        deleteUser(client, userId)
    }

    @Test
    fun `can place ship`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

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
            .expectStatus().isCreated

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }
}
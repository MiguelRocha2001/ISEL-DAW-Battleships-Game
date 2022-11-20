package pt.isel.daw.dawbattleshipgame.http.game

import org.junit.jupiter.api.Assertions
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.utils.getCreateGameInputModel
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.util.*


data class GameInfo(val gameId: Int, val player1Id : Int, val player1Token: String, val player2Id : Int, val player2Token: String)

/**
 * Creates two users and starts a game with them.
 * Also, asserts if the behavior is correct.
 * @return the game id along with the two tokens
 */
internal fun createGame(client: WebTestClient): GameInfo {
    val gameConfig = getCreateGameInputModel()

    val player1 = createUser(client)
    val player1Id = player1.first
    val player1Token = player1.second

    val player2 = createUser(client)
    val player2Id = player2.first
    val player2Token = player2.second

    // player 1 will try to create a game, and will be put in the waiting list
    client.post().uri(Uris.Games.My.ALL)
        .bodyValue(gameConfig)
        .header("Authorization", "Bearer $player1Token")
        .exchange()
        .expectStatus().isAccepted

    // player 2 should be able to create a game
    client.post().uri(Uris.Games.My.ALL)
        .bodyValue(gameConfig)
        .header("Authorization", "Bearer $player2Token")
        .exchange()
        .expectStatus().isCreated

    // player 1 should be able to get the game
    val gameId1Siren = client.get().uri(Uris.Games.My.CURRENT)
        .header("Authorization", "Bearer $player1Token")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    // assertEquals("Game", gameId1Siren.)
    val gameId1 = (gameId1Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: Assertions.fail("Game id is null")

    // player 2 should be able to get the game
    val gameId2Siren = client.get().uri(Uris.Games.My.CURRENT)
        .header("Authorization", "Bearer $player2Token")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    Assertions.assertNotNull(gameId1Siren)
    val gameId2 = (gameId2Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: Assertions.fail("Game id is null")

    Assertions.assertEquals(gameId1, gameId2)

    return GameInfo(gameId1, player1Id, player1Token, player2Id, player2Token)
}


internal fun deleteGame(client: WebTestClient, gameId: Int) {
    client.delete().uri(Uris.Games.BY_ID, gameId)
        .exchange()
        .expectStatus().isOk
}

internal fun createUser( client: WebTestClient): Pair<Int, String> {

    // a random user
    val username = UUID.randomUUID().toString()
    val password = getRandomPassword()

    // when: creating a user
    // then: the response is a 201 with a proper Location header
    val siren = client.post().uri(Uris.Users.ALL)
        .bodyValue(
            mapOf(
                "username" to username,
                "password" to password
            )
        )
        .exchange()
        .expectStatus().isCreated
        .expectHeader().value("location") {
            Assertions.assertTrue(it.startsWith("/users/"))
        }
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    Assertions.assertNotNull(siren)
    val userId = (siren.properties as LinkedHashMap<String, *>)["userId"] as? Int ?: Assertions.fail("Game id is null")

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
        .responseBody ?: Assertions.fail("No response body")

    val token = (result.properties as LinkedHashMap<String, String>)["token"] ?: Assertions.fail("No token")
    return userId to token
}


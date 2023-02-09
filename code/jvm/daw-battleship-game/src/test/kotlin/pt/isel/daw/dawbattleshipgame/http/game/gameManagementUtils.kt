package pt.isel.daw.dawbattleshipgame.http.game

import org.junit.jupiter.api.Assertions
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.utils.getCreateGameInputModel
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.lang.Thread.sleep
import org.springframework.http.HttpHeaders
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
        .header(HttpHeaders.COOKIE, "token=$player1Token")
        .exchange()
        .expectStatus().isAccepted

    // player 2 should be able to create a game
    client.post().uri(Uris.Games.My.ALL)
        .bodyValue(gameConfig)
        .header(HttpHeaders.COOKIE, "token=$player2Token")
        .exchange()
        .expectStatus().isCreated

    // player 1 should be able to get the game
    val gameId1Siren = client.get().uri(Uris.Games.My.CURRENT)
        .header(HttpHeaders.COOKIE, "token=$player1Token")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    // assertEquals("Game", gameId1Siren.)
    val gameId1 = (gameId1Siren.properties as LinkedHashMap<String, *>)["id"] as? Int ?: Assertions.fail("Game id is null")

    // player 2 should be able to get the game
    val gameId2Siren = client.get().uri(Uris.Games.My.CURRENT)
        .header(HttpHeaders.COOKIE, "token=$player2Token")
        .exchange()
        .expectStatus().isOk
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    Assertions.assertNotNull(gameId1Siren)
    val gameId2 = (gameId2Siren.properties as LinkedHashMap<String, *>)["id"] as? Int ?: Assertions.fail("Game id is null")

    Assertions.assertEquals(gameId1, gameId2)
    return GameInfo(gameId1, player1Id, player1Token, player2Id, player2Token)
}


internal fun deleteGame(client: WebTestClient, gameId: Int) {
    client.delete().uri(Uris.Games.BY_ID1, gameId)
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
    sleep(500)

    val token = (result.properties as LinkedHashMap<String, String>)["token"] ?: Assertions.fail("No token")
    return userId to token
}

internal fun placeSomeShips(client: WebTestClient, playerToken: String){
    client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
        .bodyValue(
            mapOf(
                "operation" to "place-ships",
                "ships" to listOf(
                    mapOf(
                        "shipType" to "CARRIER",
                        "position" to mapOf(
                            "row" to 1,
                            "column" to 1
                        ),
                        "orientation" to "HORIZONTAL"
                    ),
                    mapOf(
                        "shipType" to "SUBMARINE",
                        "position" to mapOf(
                            "row" to 3,
                            "column" to 1
                        ),
                        "orientation" to "VERTICAL"
                    ),
                    mapOf(
                        "shipType" to "BATTLESHIP",
                        "position" to mapOf(
                            "row" to 10,
                            "column" to 4
                        ),
                        "orientation" to "HORIZONTAL"
                    ),
                    mapOf(
                        "shipType" to "DESTROYER",
                        "position" to mapOf(
                            "row" to 6,
                            "column" to 6
                        ),
                        "orientation" to "VERTICAL"
                    ),
                    mapOf(
                        "shipType" to "CRUISER",
                        "position" to mapOf(
                            "row" to 1,
                            "column" to 7
                        ),
                        "orientation" to "HORIZONTAL"
                    )
                )
            )
        )
        .header(HttpHeaders.COOKIE, "token=$playerToken")
        .exchange()
        .expectStatus().isCreated
    sleep(500)

}


internal fun confirmPlayerFleet(playerToken:String,client: WebTestClient){
    client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
        .bodyValue(
            mapOf(
                "fleetConfirmed" to "true",
            )
        )
        .header(HttpHeaders.COOKIE, "token=$playerToken")
        .exchange()
        .expectStatus().isNoContent
    sleep(500)
}
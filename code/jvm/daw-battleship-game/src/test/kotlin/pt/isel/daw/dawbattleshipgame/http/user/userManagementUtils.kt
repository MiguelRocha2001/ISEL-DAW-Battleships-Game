package pt.isel.daw.dawbattleshipgame.http

import org.junit.jupiter.api.Assertions
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import java.util.*

internal fun deleteUser(client: WebTestClient, userId: Int) {
    client.delete().uri("/users/$userId")
        .exchange()
        .expectStatus().isEqualTo(204)
}

internal fun createUser(username: String, password: String, client: WebTestClient): Int {
    // when: creating an user
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
            Assertions.assertTrue(it.startsWith("/users/"))
        }
        .expectHeader().contentType("application/json")
        .expectBody(SirenModel::class.java)
        .returnResult()
        .responseBody ?: Assertions.fail("Game id is null")

    Assertions.assertNotNull(siren)
    val userId = (siren.properties as LinkedHashMap<String, *>)["userId"] as? Int ?: Assertions.fail("Game id is null")

    // verify links
    val links = siren.links
    Assertions.assertNotNull(links)
    Assertions.assertEquals(1, links.size)
    Assertions.assertEquals("self", links[0].rel[0])
    Assertions.assertEquals("/users", links[0].href)

    // verify actions
    val actions = siren.actions
    Assertions.assertNotNull(actions)
    Assertions.assertEquals(1, actions.size)
    Assertions.assertEquals("create-token", actions[0].name)
    Assertions.assertEquals("/users/token", actions[0].href)
    Assertions.assertEquals("POST", actions[0].method)
    Assertions.assertEquals("application/json", actions[0].type)
    Assertions.assertEquals(2, actions[0].fields.size)
    Assertions.assertEquals("username", actions[0].fields[0].name)
    Assertions.assertEquals("text", actions[0].fields[0].type)
    Assertions.assertEquals("password", actions[0].fields[1].name)
    Assertions.assertEquals("text", actions[0].fields[1].type)

    return userId
}

internal fun createUserAndToken(username: String, password: String, client: WebTestClient): Pair<Int, String> { //Fixme:merge this with the copy in gameTests.kt
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


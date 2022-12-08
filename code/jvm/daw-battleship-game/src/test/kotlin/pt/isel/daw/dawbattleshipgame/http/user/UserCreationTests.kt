package pt.isel.daw.dawbattleshipgame.http.user

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.createUser
import pt.isel.daw.dawbattleshipgame.http.deleteUser
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserCreationTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration {
        @Bean
        @Primary
        fun jdbiTest() = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(System.getenv("DB_POSTGRES_BATTLESHIPS_TESTS_CONNECTION"))
            }
        ).configure()
    }

    @Test
    fun canCreateUser() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()
        val userId = createUser(username, password, client)
        deleteUser(client, userId)
    }

    @Test
    fun `can create an user, obtain a token, and access user home`() { //fixme: I think this test is not needed or should be in another class
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        val userId = createUser(username, password, client)

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri(Uris.Users.TOKEN)
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
            .responseBody!!

        assertNotNull(result.properties)
        val token = (result.properties as LinkedHashMap<String, String>)["token"]

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        val userHome = client.get().uri(Uris.Users.HOME)
            .header("Authorization", "Bearer ${token}")
            .exchange()
            .expectStatus().isCreated
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No response body")

        val properties = userHome.properties as LinkedHashMap<String, *>
        assertEquals(userId, properties["userId"] as? Int)
        assertEquals(username, properties["username"])

        // asserting links
        val links = userHome.links
        assertEquals(2, links.size)

        assertEquals("/me", links[0].href)
        assertEquals(1, links[0].rel.size)
        assertEquals("self", links[0].rel[0])

        assertEquals("/my/games/current", links[1].href)
        assertEquals(1, links[1].rel.size)
        assertEquals("game-id", links[1].rel[0])

        // asserting actions
        val actions = userHome.actions
        assertEquals(1, actions.size)

        assertEquals("start-game", actions[0].name)
        assertEquals("/my/games", actions[0].href)
        assertEquals("POST", actions[0].method)
        assertEquals("application/json", actions[0].type)


        // when: getting the user home with an invalid token
        // then: the response is a 4001 with the proper problem
        client.get().uri(Uris.Users.HOME)
            .header("Authorization", "Bearer ${token}-invalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")

        deleteUser(client, userId)
    }

    @Test
    fun `user creation produces an error if user already exists`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        val userId = createUser(username, password, client)

        // when: creating the same user again
        // then: the response is a 400 with the proper tyoe
        client.post().uri(Uris.Users.ALL)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType("application/problem+json")
            .expectBody()

        deleteUser(client, userId)
    }

    @Test
    fun `user creation with blank username`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val password = "superSecurePassword"

        // when: creating a user
        // then: the response is a 400 with the proper type
        client.post().uri(Uris.Users.ALL)
            .bodyValue(
                mapOf(
                    "username" to "",
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/invalid-username")
    }

    @Test
    fun `user creation produces an error if password is weak`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = "-"

        // when: creating a user
        // then: the response is a 400 with the proper type
        client.post().uri(Uris.Users.ALL)
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

}
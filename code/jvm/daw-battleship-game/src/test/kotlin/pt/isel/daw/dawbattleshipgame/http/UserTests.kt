package pt.isel.daw.dawbattleshipgame.http

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
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {

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

    fun createUser(username: String, password: String, client: WebTestClient): Int {
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
                assertTrue(it.startsWith("/users/"))
            }
            .expectHeader().contentType("application/json")
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("Game id is null")

        assertNotNull(siren)
        val userId = (siren.properties as LinkedHashMap<String, *>)["userId"] as? Int ?: fail("Game id is null")

        // verify links
        val links = siren.links
        assertNotNull(links)
        assertEquals(1, links.size)
        assertEquals("self", links[0].rel[0])
        assertEquals("/users", links[0].href)

        // verify actions
        val actions = siren.actions
        assertNotNull(actions)
        assertEquals(1, actions.size)
        assertEquals("create-token", actions[0].name)
        assertEquals("/users/token", actions[0].href)
        assertEquals("POST", actions[0].method)
        assertEquals("application/json", actions[0].type)
        assertEquals(2, actions[0].fields.size)
        assertEquals("username", actions[0].fields[0].name)
        assertEquals("text", actions[0].fields[0].type)
        assertEquals("password", actions[0].fields[1].name)
        assertEquals("text", actions[0].fields[1].type)

        return userId
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
    fun `can create an user, obtain a token, and access user home`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        val userId = createUser(username, password, client)

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
            .responseBody!!

        assertNotNull(result.properties)
        val token = (result.properties as LinkedHashMap<String, String>)["token"]

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        val userHome = client.get().uri("/me")
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
        assertEquals("/games", actions[0].href)
        assertEquals("POST", actions[0].method)
        assertEquals("application/json", actions[0].type)


        // when: getting the user home with an invalid token
        // then: the response is a 4001 with the proper problem
        client.get().uri("/me")
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
        client.post().uri("/users")
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
    fun `user creation produces an error if password is weak`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = "-"

        // when: creating a user
        // then: the response is a 400 with the proper type
        client.post().uri("/users")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `requesting token with wrong password or username`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()
        val userId = createUser(username, password, client)

        // when: requesting a token with wrong username
        // then: the response is a 403 with the proper type
        client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username + "wrong",
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType("application/problem+json")
            .expectBody()

        // when: requesting a token with wrong password
        // then: the response is a 403 with the proper type
        client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password + "wrong"
                )
            )
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType("application/problem+json")
            .expectBody()

        deleteUser(client, userId)
    }

    @Test
    fun `requesting token with black password or username`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()
        val userId = createUser(username, password, client)

        // when: requesting a token with wrong username
        // then: the response is a 403 with the proper type
        client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to "",
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType("application/problem+json")
            .expectBody()

        // when: requesting a token with wrong password
        // then: the response is a 403 with the proper type
        client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to ""
                )
            )
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().contentType("application/problem+json")
            .expectBody()

        deleteUser(client, userId)
    }


}
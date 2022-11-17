package pt.isel.daw.dawbattleshipgame.http.user

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.createUser
import pt.isel.daw.dawbattleshipgame.http.deleteUser
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.util.*
import kotlin.collections.ArrayList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTokenTests {

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
    fun `can create a token for a user`() {
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

        Assertions.assertNotNull(result.properties)
        val token = (result.properties as LinkedHashMap<String, String>)["token"]
        Assertions.assertNotNull(token)
        val clazz = (result.clazz as ArrayList<String>).first()
        Assertions.assertEquals(clazz, "user-token")
        deleteUser(client, userId)
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
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
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getRandomPassword
import java.util.*
import org.springframework.http.HttpHeaders

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserGetTests {

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
    fun `creates a user,gets it,deletes ans gets it again`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        // when: creating a user
        val creationResult = createUserAndToken(username, password, client)
        val id = creationResult.first
        val token = creationResult.second
        val beforeDeletion = client.get().uri(Uris.Users.HOME)
            .header(HttpHeaders.COOKIE, "token=$token")
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(SirenMediaType)
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: Assertions.fail("No response body")

        Assertions.assertNotNull(beforeDeletion)

        // when: deleting the user
        deleteUser(client,id)

        val afterDeletion = client.get().uri(Uris.Users.HOME)
            //add cookie
            .header(HttpHeaders.COOKIE, "token=$token")
            .exchange()
            .expectStatus().isUnauthorized

    }

@Test
    fun `get to user home with invalid token`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        // and: a random user
        val username = UUID.randomUUID().toString()
        val password = getRandomPassword()

        // when: creating a user
        val creationResultId = createUser(username, password, client)
        val token = "invalid token"
        client.get().uri(Uris.Users.HOME)
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isUnauthorized
        deleteUser(client, creationResultId)
    }

}
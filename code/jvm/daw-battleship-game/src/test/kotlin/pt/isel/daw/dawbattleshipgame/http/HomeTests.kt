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
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeTests {

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
    fun `can create an user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        val siren = client.get().uri("/")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType("application/json")
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No response body")

        assertNotNull(siren)

        // asserting properties
        val title = (siren.properties as LinkedHashMap<String, *>)["title"] as? String ?: fail("Credits is in fault or not a string")
        assertEquals("Welcome to the Battleship Game API", title)

        // asserting links
        val links = siren.links
        assertEquals(1, links.size)
        assertEquals("/", links[0].href)
        assertEquals(1, links[0].rel.size)
        assertEquals("self", links[0].rel[0])

        // asserting actions
        val actions = siren.actions
        assertEquals(2, actions.size)

        assertEquals("create-user", actions[0].name)
        assertEquals("/users", actions[0].href)
        assertEquals("POST", actions[0].method)
        assertEquals("application/json", actions[0].type)

        assertEquals("login", actions[1].name)
        assertEquals("/users/token", actions[1].href)
        assertEquals("POST", actions[1].method)
        assertEquals("application/json", actions[1].type)
    }
}
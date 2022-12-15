package pt.isel.daw.dawbattleshipgame.http.home

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeTests {

    // One of the very few places where we use property injection
    @LocalServerPort //TODO PERGUNTAR SE É PRECISO TER ESTA TRALHA TODA EM CADA TESTE
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
    fun `home menu`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        val siren = client.get().uri(Uris.Home.HOME)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(SirenMediaType)
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No response body")

        assertNotNull(siren)

        // asserting properties
        val title = (siren.properties as LinkedHashMap<String, *>)["title"] as? String
            ?: fail("Credits is in fault or not a string")
        assertEquals("Welcome to the Battleship Game API", title)

        // asserting links
        val links = siren.links
        assertEquals(4, links.size)

        assertEquals("/", links[0].href)
        assertEquals(1, links[0].rel.size)
        assertEquals("self", links[0].rel[0])

        assertEquals("/users/all/statistics", links[1].href)
        assertEquals(1, links[1].rel.size)
        assertEquals("user-stats", links[1].rel[0])

        assertEquals("/info", links[2].href)
        assertEquals(1, links[2].rel.size)
        assertEquals("server-info", links[2].rel[0])

        assertEquals("/me", links[3].href)
        assertEquals(1, links[3].rel.size)
        assertEquals("user-home", links[3].rel[0])

        // asserting actions
        val actions = siren.actions
        assertEquals(2, actions.size)

        assertEquals("create-user", actions[0].name)
        assertEquals("/users", actions[0].href)
        assertEquals(HttpMethod.POST, actions[0].method)
        assertEquals("application/json", actions[0].type)

        assertEquals("create-token", actions[1].name)
        assertEquals("/users/token", actions[1].href)
        assertEquals(HttpMethod.POST, actions[1].method)
        assertEquals("application/json", actions[1].type)
    }

    @Test
    fun getServerInfo() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        // when: getting the server info
        // then: the response is a 200 with a proper Location header
        val siren = client.get().uri(Uris.Home.SERVER_INFO)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType("application/json")
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: fail("No response body")

        assertNotNull(siren)

        // asserting properties
        val authors = (siren.properties as LinkedHashMap<String, *>)["authors"] as? List<LinkedHashMap<String, String>>
            ?: fail("Authors is in fault or not a string")

        // asserts authors
        assertEquals(3, authors.size)

        assertTrue { authors.all { it.size == 2 } } // this indicates that each author has only 2 properties (name and email)
        assertEquals("António Carvalho", authors[0]["name"])
        assertEquals("A48347@alunos.isel.pt", authors[0]["email"])
        assertEquals("Pedro Silva", authors[1]["name"])
        assertEquals("A47128@alunos.isel.pt", authors[1]["email"])
        assertEquals("Miguel Rocha", authors[2]["name"])
        assertEquals("A47185@alunos.isel.pt", authors[2]["email"])

        // confirms that the version is really a String
        (siren.properties as LinkedHashMap<String, *>)["systemVersion"] as? String
            ?: fail("SystemVersion is in fault or not a string")

        // asserting links
        val links = siren.links
        assertEquals(1, links.size)

        assertEquals("/info", links[0].href)
        assertEquals(1, links[0].rel.size)
        assertEquals("self", links[0].rel[0])
    }
}
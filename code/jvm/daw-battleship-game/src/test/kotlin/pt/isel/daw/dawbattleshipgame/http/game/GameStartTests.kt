package pt.isel.daw.dawbattleshipgame.http.game

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.user.deleteUser
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getCreateGameInputModel


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameStartTests {
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
        client.post().uri(Uris.Games.My.ALL)
            .bodyValue(gameConfig)
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-game")

        // player 2 tries to create another game
        client.post().uri(Uris.Games.My.ALL)
            .bodyValue(gameConfig)
            .header(HttpHeaders.COOKIE, "token=$player2Token")
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

        val user = createUser(client)
        val userId = user.first
        val userToken = user.second
        val gameConfig = getCreateGameInputModel()

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri(Uris.Games.My.ALL)
            .bodyValue(gameConfig)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isAccepted

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri(Uris.Games.My.ALL)
            .bodyValue(gameConfig)
            .header(HttpHeaders.COOKIE, "token=$userToken")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader().contentType("application/problem+json")
            .expectBody()
            .jsonPath("type")
            .isEqualTo("https://github.com/isel-leic-daw/2022-daw-leic52d-2-22-daw-leic52d-g11/docs/problem/user-already-in-queue")

        deleteUser(client, userId)
    }

    @Test
    fun `non valid user tries to starts a game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val nonValidUserToken = "DGykwxMgqS8_JshvyLveWVXt2UywYHt7A4pMKHcpmk4="
        val gameConfig = getCreateGameInputModel()

        // Invalid user will try to create a game, without success
        client.post().uri(Uris.Games.My.ALL)
            .bodyValue(gameConfig)
            .header(HttpHeaders.COOKIE, "token=$nonValidUserToken")
            .exchange()
            .expectStatus().isUnauthorized

    }
}
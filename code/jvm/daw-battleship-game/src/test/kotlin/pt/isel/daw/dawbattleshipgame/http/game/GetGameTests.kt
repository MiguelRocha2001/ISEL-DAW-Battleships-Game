package pt.isel.daw.dawbattleshipgame.http.game

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
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.http.user.deleteUser
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetGameTests {
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
    fun `get existing game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id

        // player 1 should be able to get the game
        client.get().uri(Uris.Games.BY_ID1, gameId)
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: Assertions.fail("Game id is null")

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `get invalid game results in error`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val invalidGameId = 12345
        val user = createUser(client)
        val userId = user.first
        val userToken = user.second

        // player 1 should be able to get the game
        client.get().uri(Uris.Games.BY_ID1, invalidGameId)
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isNotFound

        deleteUser(client, userId)
    }

    @Test
    fun `get existing current game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id

        // player 1 should be able to get the game
        client.get().uri(Uris.Games.My.CURRENT)
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectBody(SirenModel::class.java)
            .returnResult()
            .responseBody ?: Assertions.fail("Game id is null")

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `get invalid current game results in error`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val user = createUser(client)
        val userId = user.first
        val userToken = user.second

        // player 1 should be able to get the game
        client.get().uri(Uris.Games.My.CURRENT)
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isNotFound

        deleteUser(client, userId)
    }
}
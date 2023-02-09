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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameRotateShipTests {
    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class GameTestConfiguration { //TODO: Fix and categorize/organize tests
        @Bean
        @Primary
        fun jdbiTest() = Jdbi.create(
            PGSimpleDataSource().apply {
                setURL(System.getenv("DB_POSTGRES_BATTLESHIPS_TESTS_CONNECTION"))
            }
        ).configure()
    }


    @Test
    fun `valid rotation`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id
        val player2Token = gameInfo.player2Token


        placeSomeShips(client, player1Token)
        placeSomeShips(client, player2Token)


        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "alter-ship",
                    "origin" to mapOf("row" to "6", "column" to "6"),
                    "destination" to null,
                )
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isNoContent

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `non valid rotation,rotating a place without ship`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id
        val player2Token = gameInfo.player2Token


        placeSomeShips(client, player1Token)
        placeSomeShips(client, player2Token)


        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "alter-ship",
                    "origin" to mapOf("row" to "9", "column" to "4"),
                    "destination" to null,
                )
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().is4xxClientError

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)

    }

    @Test
    fun `invalid rotation,trying to rotate ship that cant`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id
        val player2Token = gameInfo.player2Token


        placeSomeShips(client, player1Token)
        placeSomeShips(client, player2Token)


        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "alter-ship",
                    "origin" to mapOf("row" to "6", "column" to "6"),
                    "destination" to null,
                )
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isNoContent

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }



}
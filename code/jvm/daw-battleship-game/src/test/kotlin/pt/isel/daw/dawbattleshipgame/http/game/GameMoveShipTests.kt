package pt.isel.daw.dawbattleshipgame.http.game

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.deleteUser
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import pt.isel.daw.dawbattleshipgame.utils.getCreateGameInputModel


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameMoveShipTests {
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
    fun `valid moving`() {
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
                    "destination" to mapOf("row" to "6", "column" to "7"),
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isNoContent

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `non valid move,moving a place without ship`() {
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
                    "origin" to mapOf("row" to "7", "column" to "7"),
                    "destination" to mapOf("row" to "6", "column" to "7"),
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().is4xxClientError

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)

    }

    @Test
    fun `invalid moving,trying to move ship to a place near another boat`() {
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
                    "origin" to mapOf("row" to "1", "column" to "1"),
                    "destination" to mapOf("row" to "1", "column" to "2"),
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().is4xxClientError

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }



}
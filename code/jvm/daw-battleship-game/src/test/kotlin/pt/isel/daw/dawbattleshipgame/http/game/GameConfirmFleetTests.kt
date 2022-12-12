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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameConfirmFleetTests {
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

    /**
     * Creates a User and returns the token.
     * Also, asserts if the behavior is correct.
     */
    @Test
    fun `can confirm fleet`() {
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


        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isNoContent

        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
                )
            )
            .header("Authorization", "Bearer $player2Token")
            .exchange()
            .expectStatus().isNoContent


        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }


    /**
     * Creates a User and returns the token.
     * Also, asserts if the behavior is correct.
     */
    @Test
    fun `error trying confirm twice`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id
        val player2Token = gameInfo.player2Token


        placeSomeShips(client, gameInfo.player1Token)
        placeSomeShips(client, gameInfo.player2Token)


        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isNoContent

        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
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
    fun `can confirm without authorization`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val fakeToken = "cKrSPcM-onxaqTsUuPKMOCdUjmEfsizpjj5pUgd5C6U="

        placeSomeShips(client, gameInfo.player1Token)
        placeSomeShips(client, gameInfo.player2Token)

        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
                )
            )
            .header("Authorization", "Bearer $fakeToken")
            .exchange()
            .expectStatus().isUnauthorized


        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }



    @Test
    fun `confirm ships without placing ships`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player1Token = gameInfo.player1Token
        val player2Id = gameInfo.player2Id
        val player2Token = gameInfo.player2Token



        client.put().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "fleetConfirmed" to "true",
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
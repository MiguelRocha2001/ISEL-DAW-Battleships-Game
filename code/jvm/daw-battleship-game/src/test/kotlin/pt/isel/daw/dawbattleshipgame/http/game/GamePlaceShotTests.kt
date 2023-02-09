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
class GamePlaceShotTests {
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

    /**
     * Creates a User and returns the token.
     * Also, asserts if the behavior is correct.
     */

    @Test
    fun `can place shots`() {
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

        confirmPlayerFleet(player1Token,client)
        confirmPlayerFleet(player2Token,client)

        val playerOneShot1 = Pair("1"," 1")
        val playerOneShot2 = Pair("1"," 2")
        val playerOneShot3 = Pair("1"," 3")
        val playerOneShot4 = Pair("1"," 4")
        val playerOneShot5 = Pair("1"," 5")

        client.post().uri(Uris.Games.My.Current.My.Shots.ALL)
            .bodyValue(
                mapOf(
                        "shots" to listOf(
                            mapOf(
                                "row" to playerOneShot1.first,
                                "column" to playerOneShot1.second
                            ),
                            mapOf(
                                "row" to playerOneShot2.first,
                                "column" to playerOneShot2.second
                            ),
                            mapOf(
                                "row" to playerOneShot3.first,
                                "column" to playerOneShot3.second
                            ),
                            mapOf(
                                "row" to playerOneShot4.first,
                                "column" to playerOneShot4.second
                            ),
                            mapOf(
                                "row" to playerOneShot5.first,
                                "column" to playerOneShot5.second
                            )
                        )
                )
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isBadRequest

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }


    @Test
    fun `error trying place shots twice in a row`() {
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

        confirmPlayerFleet(player1Token,client)
        confirmPlayerFleet(player2Token,client)

        val playerOneShot = Pair("1"," 1")
        val playerOneShotAgain = Pair("1"," 2")

        // tries to place only one shot (requires two)
        client.post().uri(Uris.Games.My.Current.My.Shots.ALL)
            .bodyValue(
                mapOf(
                       "shots" to listOf(
                           mapOf(
                               "row" to playerOneShot.first,
                               "column" to playerOneShot.second
                           )
                       )
                )
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isNoContent


        client.post().uri(Uris.Games.My.Current.My.Shots.ALL)
            .bodyValue(
                mapOf(
                    "shots" to listOf(
                        mapOf(
                            "row" to playerOneShotAgain.first,
                            "column" to playerOneShotAgain.second
                        )
                    )
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
    fun `error trying to shoot unauthorized `() {
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

        confirmPlayerFleet(player1Token,client)
        confirmPlayerFleet(player2Token,client)

        val playerOneShot = Pair("1"," 1")
        val playerOneShotAgain = Pair("1"," 2")
        val invalidToken = "cKrSPcM-onxaqTsUuPKMOCdUjmEfsizpjj5pUgd5C6U=cKrSPcM-onxaqTsUuPKMOCdUjmEfsizpjj5pUgd5C6U="

        // Invalid user will try to add a ship, without success
        client.post().uri(Uris.Games.My.Current.My.Shots.ALL)
            .bodyValue(
                    mapOf(
                    "shots" to listOf(mapOf(
                    "row" to playerOneShot.first,
                    "column" to playerOneShot.second
                )))
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().isUnauthorized

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }


    @Test
    fun `error trying shot without opponent confirmation`() {
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

        confirmPlayerFleet(player1Token,client)

        val playerOneShot = Pair("1"," 1")

        // Invalid user will try to add a ship, without success
        client.post().uri(Uris.Games.My.Current.My.Shots.ALL)
            .bodyValue(
                mapOf(
                        "shots" to listOf(mapOf(
                    "row" to playerOneShot.first,
                    "column" to playerOneShot.second
                )))
            )
            .header(HttpHeaders.COOKIE, "token=$player1Token")
            .exchange()
            .expectStatus().is4xxClientError

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

}
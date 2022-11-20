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
class GamePlaceShipsTests {
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
    fun `can place multiple ships`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        ),
                        mapOf(
                            "shipType" to "SUBMARINE",
                            "position" to mapOf(
                                "row" to 3,
                                "column" to 1
                            ),
                            "orientation" to "VERTICAL"
                        ),
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 10,
                                "column" to 4
                            ),
                            "orientation" to "HORIZONTAL"
                        ),
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 6,
                                "column" to 6
                            ),
                            "orientation" to "VERTICAL"
                        ),
                        mapOf(
                            "shipType" to "CRUISER",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 7
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }


    @Test
    fun `non valid user tries to place a ship`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val nonValidToken = "DGykwxMgqS8_JshvyLveWVXt2UywYHt7A4pMKHcpmk4="


        // Invalid user will try to add a ship, without success
        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 6,
                                "column" to 1
                            ),
                            "orientation" to "VERTICAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $nonValidToken")
            .exchange()
            .expectStatus().isUnauthorized

        deleteGame(client, gameId)
        deleteUser(client, gameInfo.player1Id)
        deleteUser(client, gameInfo.player2Id)
    }

    @Test
    fun `Placing ships touching boarders`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 10,
                                "column" to 8
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 6,
                                "column" to 1
                            ),
                            "orientation" to "VERTICAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `Placing the same ship twice`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 5,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `Placing ship in invalid coordinates`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 0,
                                "column" to 0
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )

            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isBadRequest // bad request because position is invalid

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "SUBMARINE",
                            "position" to mapOf(
                                "row" to 50,
                                "column" to 10
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405) // method not allowed because, even though position is valid, it is not in the board context

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `Placing ship on top of another`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "CARRIER",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 1
                            ),
                            "orientation" to "VERTICAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 2
                            ),
                            "orientation" to "VERTICAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `Placing ship touching another`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 2,
                                "column" to 3
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isCreated

        // touching above
        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 1,
                                "column" to 3
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        // touching below
        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 3,
                                "column" to 3
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        // touching left
        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 2,
                                "column" to 1
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        // touching right
        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 2,
                                "column" to 7
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }

    @Test
    fun `Placing ship half outside boards`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val gameInfo = createGame(client)
        val gameId = gameInfo.gameId
        val player1Id = gameInfo.player1Id
        val player2Id = gameInfo.player2Id
        val player1Token = gameInfo.player1Token

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "BATTLESHIP",
                            "position" to mapOf(
                                "row" to 5,
                                "column" to 8
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 10,
                                "column" to 10
                            ),
                            "orientation" to "HORIZONTAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        client.post().uri(Uris.Games.My.Current.My.Ships.ALL)
            .bodyValue(
                mapOf(
                    "operation" to "place-ship",
                    "ships" to listOf(
                        mapOf(
                            "shipType" to "DESTROYER",
                            "position" to mapOf(
                                "row" to 10,
                                "column" to 10
                            ),
                            "orientation" to "VERTICAL"
                        )
                    )
                )
            )
            .header("Authorization", "Bearer $player1Token")
            .exchange()
            .expectStatus().isEqualTo(405)

        deleteGame(client, gameId)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }
}
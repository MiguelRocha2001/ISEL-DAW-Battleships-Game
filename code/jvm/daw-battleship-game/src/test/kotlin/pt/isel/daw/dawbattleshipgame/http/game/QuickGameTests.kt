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
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.http.controllers.Uris
import pt.isel.daw.dawbattleshipgame.http.infra.SirenModel
import pt.isel.daw.dawbattleshipgame.http.user.deleteUser
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure
import java.util.LinkedHashMap


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuickGameTests {
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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `try to make a quick game with two players`() {

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        val player1 = createUser(client)
        val player1Id = player1.first
        val player1Token = player1.second

        val player2 = createUser(client)
        val player2Id = player2.first
        val player2Token = player2.second

        // player 1 will try to create a game, and will be put in the waiting list
        client.post().uri(Uris.Games.My.ALL)
                .header("Authorization", "Bearer $player1Token")
                .exchange()


        // player 2 should be able to create a game
        client.post().uri(Uris.Games.My.ALL)
                .header("Authorization", "Bearer $player2Token")
                .exchange()

        // player 1 should be able to get the game
        val gameId1Siren = client.get().uri(Uris.Games.My.CURRENT)
                .header("Authorization", "Bearer $player1Token")
                .exchange()
                .expectBody(SirenModel::class.java)
                .returnResult()
                .responseBody ?: Assertions.fail("Game id is null")

        // assertEquals("Game", gameId1Siren.)
        val gameId1 = (gameId1Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: Assertions.fail("Game id is null")

        // player 2 should be able to get the game
        val gameId2Siren = client.get().uri(Uris.Games.My.CURRENT)
                .header("Authorization", "Bearer $player2Token")
                .exchange()
                .expectStatus().isOk
                .expectBody(SirenModel::class.java)
                .returnResult()
                .responseBody ?: Assertions.fail("Game id is null")

        Assertions.assertNotNull(gameId1Siren)
        val gameId2 = (gameId2Siren.properties as LinkedHashMap<String, *>)["gameId"] as? Int ?: Assertions.fail("Game id is null")
        Assertions.assertEquals(gameId1, gameId2)
        deleteGame(client, gameId1)
        deleteUser(client, player1Id)
        deleteUser(client, player2Id)
    }


}
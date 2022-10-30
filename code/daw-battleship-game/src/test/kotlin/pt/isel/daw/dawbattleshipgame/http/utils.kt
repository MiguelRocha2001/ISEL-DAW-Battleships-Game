package pt.isel.daw.dawbattleshipgame.http

import org.springframework.test.web.reactive.server.WebTestClient

fun deleteUser(client: WebTestClient, userId: Int) {
    client.delete().uri("/users/$userId")
        .exchange()
        .expectStatus().isEqualTo(204)
}


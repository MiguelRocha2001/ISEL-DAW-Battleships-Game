package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildHomeActions
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.ServerInfoOutputModel
import pt.isel.daw.dawbattleshipgame.services.InfoServices

@RestController
class HomeController(
    private val infoServices: InfoServices
) {

    @GetMapping(Uris.HOME)
    fun getHome() = siren(HomeOutputModel("Welcome to the Battleship Game API")) {
        link(Uris.home(), Rels.SELF)
        buildHomeActions(this)
    }

    @GetMapping(Uris.SERVER_INFO)
    fun getServerInfo(): ResponseEntity<*> {
        val res = infoServices.getServerInfo()
        return ResponseEntity.status(200)
            .body(
                siren(ServerInfoOutputModel(res.authors, res.systemVersion)) {
                    link(Uris.info(), Rels.SELF)
                }
            )
    }
}
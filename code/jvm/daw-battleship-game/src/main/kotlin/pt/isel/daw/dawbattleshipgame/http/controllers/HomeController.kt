package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildHomeActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.homeLinks
import pt.isel.daw.dawbattleshipgame.http.infra.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.home.AuthorOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.ServerInfoOutputModel
import pt.isel.daw.dawbattleshipgame.services.InfoServices

@RestController
class HomeController(
    private val infoServices: InfoServices
) {
    @GetMapping(Uris.Home.HOME)
    fun getHome(): ResponseEntity<*> {
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(
                siren(HomeOutputModel("Welcome to the Battleship Game API")) {
                    homeLinks()
                    buildHomeActions(this)
                    clazz("home") //fixme: i think this is the info the class of siren object need(according to the projects I saw),but if not, change it
                }
            )
    }

    @GetMapping(Uris.Home.SERVER_INFO)
    fun getServerInfo(): ResponseEntity<*> {
        val res = infoServices.getServerInfo()
        return ResponseEntity.status(200)
            .body(
                siren(ServerInfoOutputModel(
                    res.authors.map { AuthorOutputModel(it.first, it.second) },
                    res.systemVersion
                )) {
                    link(Uris.Home.info(), Rels.SELF)
                    clazz("server-info")
                }
            )
    }
}
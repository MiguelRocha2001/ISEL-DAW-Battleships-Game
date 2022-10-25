package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildHomeActions
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.infra.siren

@RestController
class HomeController {

    @GetMapping(Uris.HOME)
    fun getHome() = siren(HomeOutputModel("Made for teaching purposes by P. Félix")) {
        link(Uris.home(), Rels.SELF)
        link(Uris.home(), Rels.HOME)
        buildHomeActions(this)
    }
}
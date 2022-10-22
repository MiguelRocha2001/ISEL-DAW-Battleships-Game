package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.hypermedia.LinkOutputModel
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction
import pt.isel.daw.dawbattleshipgame.http.hypermedia.homeSirenActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.homeSirenLinks
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import java.net.URI

@RestController
class HomeController {

    @GetMapping(Uris.HOME)
    fun getHome() = HomeOutputModel(
        sirenActions = homeSirenActions,
        links = homeSirenLinks
    )
}
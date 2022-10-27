package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildHomeActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildTokenRequestActions
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.home.ServerInfoOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.user.TokenOutputModel
import pt.isel.daw.dawbattleshipgame.services.InfoServices
import pt.isel.daw.dawbattleshipgame.services.ServerInfoError
import pt.isel.daw.dawbattleshipgame.services.user.TokenCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserServices

@RestController
class HomeController(
    private val userService: UserServices,
    private val infoServices: InfoServices
) {

    @GetMapping(Uris.HOME)
    fun getHome() = siren(HomeOutputModel("Welcome to the Battleship Game API")) {
        link(Uris.home(), Rels.SELF)
        buildHomeActions(this)
    }

    @GetMapping(Uris.SERVER_INFO)
    fun getServerInfo() {
        val res = infoServices.getServerInfo()
        when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(
                    siren(ServerInfoOutputModel(res.value.authors, res.value.systemVersion)) {
                        link(Uris.info(), Rels.SELF)
                    }
                )
            is Either.Left -> when (res.value) {

            }
        }
    }
}
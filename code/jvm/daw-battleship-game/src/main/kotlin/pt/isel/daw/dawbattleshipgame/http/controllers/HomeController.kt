package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.*
import pt.isel.daw.dawbattleshipgame.http.hypermedia.homeLinks
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.home.AuthorOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.home.ServerInfoOutputModel
import pt.isel.daw.dawbattleshipgame.services.InfoServices

@RestController
class HomeController(
    private val infoServices: InfoServices
) {
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Problem> {
        return Problem.buildInputError(ex.value.toString())
    }

    @GetMapping(Uris.Home.HOME)
    fun getHome(): ResponseEntity<*> {
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(
                siren(HomeOutputModel("Welcome to the Battleship Game API")) {
                    clazz("home")
                    homeLinks()
                    createUserSirenAction(this)
                    createTokenSirenAction(this)
                    createGameSirenAction(this)

                    preparationActions(this)
                    battleActions(this)

                    quitGameAction(this)
                    quitGameQueueAction(this)
                }
            )
    }

    @GetMapping(Uris.Home.SERVER_INFO)
    fun getServerInfo(): ResponseEntity<*> {
        val res = infoServices.getServerInfo()
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
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
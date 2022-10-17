package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.model.home.HomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.LinkOutputModel

@RestController
class HomeController {

    @GetMapping(Uris.HOME)
    fun getHome() = HomeOutputModel(
        links = listOf(
            LinkOutputModel(
                Uris.home(),
                LinkRelation.SELF
            ),
            LinkOutputModel(
                Uris.serverInfo(),
                LinkRelation.SERVER_INFO
            ),
            LinkOutputModel(
                Uris.battleshipsStatistics(),
                LinkRelation.BATTLESHIPS_STATISTICS
            ),
        )
    )
}
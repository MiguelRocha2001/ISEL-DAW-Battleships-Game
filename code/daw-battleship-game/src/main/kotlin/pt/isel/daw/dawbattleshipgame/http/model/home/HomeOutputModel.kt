package pt.isel.daw.dawbattleshipgame.http.model.home

import pt.isel.daw.dawbattleshipgame.http.hypermedia.LinkOutputModel
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction


data class HomeOutputModel(
    val _class: String = "home",
    val sirenActions: List<SirenAction>,
    val links: List<LinkOutputModel>,
)
package pt.isel.daw.dawbattleshipgame.http.model.home

import pt.isel.daw.dawbattleshipgame.http.model.LinkOutputModel


data class HomeOutputModel(
    val _class: String = "home",
    val links: List<LinkOutputModel>,
)
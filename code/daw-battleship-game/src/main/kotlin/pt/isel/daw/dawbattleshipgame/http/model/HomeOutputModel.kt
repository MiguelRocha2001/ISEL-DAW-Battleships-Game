package pt.isel.daw.dawbattleshipgame.http.model


data class HomeOutputModel(
    val _class: String = "home",
    val links: List<LinkOutputModel>,
)
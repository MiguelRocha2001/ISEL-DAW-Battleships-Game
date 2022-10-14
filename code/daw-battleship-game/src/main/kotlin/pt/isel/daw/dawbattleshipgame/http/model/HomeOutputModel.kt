package pt.isel.daw.tictactow.http.model

import pt.isel.daw.dawbattleshipgame.http.model.LinkOutputModel

data class HomeOutputModel(
    val links: List<LinkOutputModel>,
)
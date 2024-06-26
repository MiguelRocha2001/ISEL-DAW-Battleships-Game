package pt.isel.daw.dawbattleshipgame.http.model.home

data class HomeOutputModel(
    val title: String
)

data class ServerInfoOutputModel(
    val authors: List<AuthorOutputModel>,
    val systemVersion: String
)

data class AuthorOutputModel(
    val name: String,
    val email: String
)
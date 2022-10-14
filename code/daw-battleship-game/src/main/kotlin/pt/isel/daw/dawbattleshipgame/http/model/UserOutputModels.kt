package pt.isel.daw.dawbattleshipgame.http.model

class UserTokenCreateOutputModel(
    val token: String
)

class UserHomeOutputModel(
    val id: String,
    val username: String,
)
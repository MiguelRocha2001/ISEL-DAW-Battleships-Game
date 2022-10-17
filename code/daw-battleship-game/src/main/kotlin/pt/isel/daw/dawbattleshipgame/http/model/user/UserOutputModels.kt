package pt.isel.daw.dawbattleshipgame.http.model.user

class UserTokenCreateOutputModel(
    val token: String
)

class UserHomeOutputModel(
    val id: String,
    val username: String,
)
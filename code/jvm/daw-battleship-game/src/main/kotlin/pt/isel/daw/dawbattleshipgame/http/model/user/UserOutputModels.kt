package pt.isel.daw.dawbattleshipgame.http.model.user

data class UserCreateOutputModel(val userId: Int)

data class TokenOutputModel(val token: String)

data class UserHomeOutputModel(
    val id: String,
    val username: String,
)
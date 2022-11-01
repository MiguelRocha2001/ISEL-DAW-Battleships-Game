package pt.isel.daw.dawbattleshipgame.http.model.user

data class UserCreateOutputModel(val userId: Int)

data class TokenOutputModel(val token: String)

data class UserHomeOutputModel(
    val userId: Int,
    val username: String,
)
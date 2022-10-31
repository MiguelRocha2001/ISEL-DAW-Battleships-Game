package pt.isel.daw.dawbattleshipgame.http.model.user

data class UserCreateInputModel(
    val username: String,
    val password: String,
)

data class UserCreateTokenInputModel(
    val username: String,
    val password: String,
)
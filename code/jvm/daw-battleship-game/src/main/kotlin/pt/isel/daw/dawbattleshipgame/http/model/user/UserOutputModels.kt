package pt.isel.daw.dawbattleshipgame.http.model.user

import pt.isel.daw.dawbattleshipgame.domain.player.UserInfo

data class UserCreateOutputModel(val userId: Int)

data class TokenOutputModel(val token: String)

data class UserListOutputModel(val users: List<UserOutputModel>)

fun UserInfo.toUserOutputModel() = UserOutputModel(id, username)

data class UserOutputModel(val userId: Int, val username: String)

data class UserHomeOutputModel(
    val userId: Int,
    val username: String,
)
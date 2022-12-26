package pt.isel.daw.dawbattleshipgame.http.model.user

import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.domain.player.UserInfo
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking

data class UserCreateOutputModel(val userId: Int)

data class TokenOutputModel(val token: String)

data class UserListOutputModel(val users: List<UserOutputModel>)

data class UserOutputModel(val id: Int, val username: String)

fun UserInfo.toUserOutputModel() = UserOutputModel(id, username)

/**
 * Represents a list of Users, with their respective number of games played and score
 */
data class UserStatsOutputModel(val users: List<UserStatOutputModel>)
data class UserStatOutputModel(val id: Int, val username: String, val wins: Int, val gamesPlayed: Int)

fun UserRanking.toUserStatOutputModel() = UserStatOutputModel(id, username, wins, gamesPlayed)
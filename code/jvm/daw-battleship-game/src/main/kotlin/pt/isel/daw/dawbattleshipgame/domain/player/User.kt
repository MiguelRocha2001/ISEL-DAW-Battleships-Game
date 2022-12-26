package pt.isel.daw.dawbattleshipgame.domain.player



data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val gamesPlayed : Int = 0,
    val wins : Int = 0,
)

fun User.toUserInfo() = UserInfo(id, username)

data class UserInfo(
    val id: Int,
    val username: String,
)


data class UserRanking(val id: Int, val username: String, val wins: Int, val gamesPlayed: Int)
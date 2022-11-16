package pt.isel.daw.dawbattleshipgame.domain.player



data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    val gamesPlayed : Int = 0,
    val wins : Int = 0,
)


data class UserRanking(val username: String, val wins: Int, val gamesPlayed: Int)
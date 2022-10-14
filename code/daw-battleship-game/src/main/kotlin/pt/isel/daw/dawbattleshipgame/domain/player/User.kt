package pt.isel.daw.dawbattleshipgame.domain.player

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
)
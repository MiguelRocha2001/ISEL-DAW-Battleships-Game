package pt.isel.daw.dawbattleshipgame.domain.player

import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
)
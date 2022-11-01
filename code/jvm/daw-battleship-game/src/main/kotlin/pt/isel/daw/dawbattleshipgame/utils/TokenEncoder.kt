package pt.isel.daw.dawbattleshipgame.utils

import pt.isel.daw.dawbattleshipgame.domain.player.TokenValidationInfo


interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
    fun validate(validationInfo: TokenValidationInfo, token: String): Boolean
}
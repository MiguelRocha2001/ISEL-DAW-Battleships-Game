package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.TokenValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.User

interface UsersRepository {

    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
    ): Boolean

    fun getUserByUsername(username: String): User?

    fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(userId: Int, token: TokenValidationInfo)
}
package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.TokenValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.User

interface UsersRepository {

    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
    ): String

    fun getUserByUsername(username: String): User?

    fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User?

    fun isUserStoredByUsername(username: String): Boolean

    fun createToken(userId: Int, token: TokenValidationInfo)
    fun getFirstUserInQueue(): Int?
    fun removeUserFromQueue(userWaiting: Int)
    fun isAlreadyInQueue(userId: Int): Boolean
}
package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.*

interface UsersRepository {
    fun getAllUsers(): List<UserInfo>
    fun clearAll()
    fun storeUser(
        username: String,
        passwordValidation: PasswordValidationInfo,
    ): Int
    fun getUsersRanking(page: Int, pageSize: Int): List<UserRanking>
    fun getUserByUsername(username: String): User?
    fun getUserById(id: Int): UserRanking?
    fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User?
    fun isUserStoredByUsername(username: String): Boolean
    fun createToken(userId: Int, token: TokenValidationInfo)
    fun getFirstUserInQueue(): Int?
    fun getFirstUserWithSameConfigInQueue(config: Configuration) : Int?
    fun getConfigFromUserQueue(userId: Int) : Configuration?
    fun removeUserFromQueue(userWaiting: Int)
    fun isAlreadyInQueue(userId: Int): Boolean
    fun insertInGameQueue(userId: Int, config : Configuration): Boolean
    fun deleteUser(userId: Int)
    fun isUserStoredById(userId: Int): Boolean
    fun deleteToken(userId: Int)
    fun isInQueue(userId: Int): Boolean
}
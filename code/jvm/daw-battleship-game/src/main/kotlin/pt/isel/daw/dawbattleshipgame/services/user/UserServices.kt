package pt.isel.daw.dawbattleshipgame.services.user


import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.UserLogic
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.utils.TokenEncoder


@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val userLogic: UserLogic,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
) {
    fun createUser(username: String, password: String): UserCreationResult {
        if (!userLogic.isSafePassword(password)) {
            return Either.Left(UserCreationError.InsecurePassword)
        }

        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode(password)
        )

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                Either.Left(UserCreationError.UserAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, passwordValidationInfo)
                Either.Right(id)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: return@run userNotFound()
            if (!passwordEncoder.matches(password, user.passwordValidation.validationInfo)) {
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val token = userLogic.generateToken()
            usersRepository.createToken(user.id, tokenEncoder.createValidationInformation(token))
            Either.Right(token)
        }
    }

    fun getUserRanking() : List<UserRanking> {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUsersRanking()
        }
    }

    fun getUserByToken(token: String): User? {
        if (!userLogic.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
            usersRepository.getUserByTokenValidationInfo(tokenValidationInfo)
        }
    }

    private fun userNotFound(): TokenCreationResult {
        passwordEncoder.encode("changeit")
        return Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
    }

    fun deleteUser(userId: Int): UserDeletionResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val gamesRepository = it.gamesRepository
            if (usersRepository.isUserStoredById(userId)) {
                gamesRepository.removeUserFromGame(userId)
                usersRepository.deleteToken(userId)
                usersRepository.deleteUser(userId)
                Either.Right(Unit)
            } else {
                Either.Left(UserDeletionError.UserDoesNotExist)
            }
        }
    }
}
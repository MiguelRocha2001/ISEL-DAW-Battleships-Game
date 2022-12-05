package pt.isel.daw.dawbattleshipgame.services.user


import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger("UserServices")

    fun createUser(username: String, password: String): UserCreationResult {
        if (!userLogic.isSafePassword(password)) {
            logger.info("User creation failed: unsafe password")
            return Either.Left(UserCreationError.InsecurePassword)
        }
        if(username.isBlank()) {
            logger.info("User creation failed: username is blank")
            return Either.Left(UserCreationError.InvalidUsername)
        }
        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode(password)
        )
        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                logger.info("User creation failed: username already exists")
                Either.Left(UserCreationError.UserAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, passwordValidationInfo)
                logger.info("User creation successful")
                Either.Right(id)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            logger.info("Token creation failed: username or password is blank")
            Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: return@run userNotFound()
                .also { logger.info("Token creation failed: user not found") }
            if (!passwordEncoder.matches(password, user.passwordValidation.validationInfo)) {
                logger.info("Token creation failed: password is invalid")
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val token = userLogic.generateToken()
            usersRepository.createToken(user.id, tokenEncoder.createValidationInformation(token))
            logger.info("Token creation successful")
            Either.Right(token)
        }
    }

    fun getUserRanking() : List<UserRanking> {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            logger.info("User ranking retrieved")
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

    fun getUserById(id: Int): User? {
        if(id < 0) return null
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUserById(id)
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
                logger.info("User deletion successful")
                Either.Right(Unit)
            } else {
                logger.info("User deletion failed: user not found")
                Either.Left(UserDeletionError.UserDoesNotExist)
            }
        }
    }
}
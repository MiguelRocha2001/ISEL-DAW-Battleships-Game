package pt.isel.daw.dawbattleshipgame.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.services.user.UserServices


@Component
class AuthorizationHeaderProcessor(
    val usersService: UserServices
) {

    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return usersService.getUserByToken(parts[1])
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME = "bearer"
    }
}
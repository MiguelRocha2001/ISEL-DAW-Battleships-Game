package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.TokenValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.repository.UsersRepository

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from _USER where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun storeUser(username: String, passwordValidation: PasswordValidationInfo): Boolean =
        handle.createUpdate(
            """
            insert into _user (username, password_validation) values (:username, :passwordvalidation)
            """
        )
            .bind("username", username)
            .bind("password_validation", passwordValidation.validationInfo)
            .execute() == 1

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from dbo.Users where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun createToken(userId: Int, token: TokenValidationInfo) {
        handle.createUpdate("insert into dbo.Tokens(user_id, token_validation) values (:user_id, :token_validation)")
            .bind("user_id", userId)
            .bind("token_validation", token.validationInfo)
            .execute()
    }

    override fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User? =
        handle.createQuery(
            """
            select id, username, password_validation 
            from dbo.Users as users 
            inner join dbo.Tokens as tokens 
            on users.id = tokens.user_id
            where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<User>()
            .singleOrNull()
}
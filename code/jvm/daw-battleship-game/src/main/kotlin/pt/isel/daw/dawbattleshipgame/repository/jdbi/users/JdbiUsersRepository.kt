package pt.isel.daw.dawbattleshipgame.repository.jdbi.users

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.player.PasswordValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.TokenValidationInfo
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking
import pt.isel.daw.dawbattleshipgame.repository.UsersRepository

class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from _USER where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserById(id: Int): User? =
        handle.createQuery("select * from _USER where id = :id")
                .bind("id", id)
                .mapTo<User>()
                .singleOrNull()

    override fun storeUser(username: String, passwordValidation: PasswordValidationInfo): Int =
        handle.createUpdate(
            """
            insert into _USER (username, password_validation) values (:username, :password_validation)
            """
        )
            .bind("username", username)
            .bind("password_validation", passwordValidation.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getUsersRanking(): List<UserRanking> {
        return handle.createQuery(
            "select username, wins, games_played from _user order by wins, games_played DESC limit 20"
        ).mapTo<UserRanking>().toList()
    }

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from _USER where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun createToken(userId: Int, token: TokenValidationInfo) {
        handle.createUpdate("delete from TOKEN where user_id = :user_id")
            .bind("user_id", userId)
            .execute()

        handle.createUpdate("insert into TOKEN(user_id, token_validation) values (:user_id, :token_validation)")
            .bind("user_id", userId)
            .bind("token_validation", token.validationInfo)
            .execute()
    }

    override fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User? =
        handle.createQuery(
            """
            select id, username, password_validation 
            from _USER as users 
            inner join TOKEN as tokens 
            on users.id = tokens.user_id
            where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<User>()
            .singleOrNull()

    override fun getFirstUserInQueue(): Int? {
        return handle.createQuery("select _user from USER_QUEUE order by priority limit 1")
            .mapTo<Int>()
            .firstOrNull()
    }

    override fun removeUserFromQueue(userWaiting: Int) {
        handle.createUpdate("delete from USER_QUEUE where _user = :_user")
            .bind("_user", userWaiting)
            .execute()
    }

    override fun isAlreadyInQueue(userId: Int): Boolean {
        return handle.createQuery("select count(*) from USER_QUEUE where _user = :_user")
            .bind("_user", userId)
            .mapTo<Int>()
            .single() != 0
    }

    override fun insertInGameQueue(userId: Int): Boolean {
        if (!isAlreadyInQueue(userId)) {
            handle.createUpdate("insert into USER_QUEUE(_user) values (:_user)")
                .bind("_user", userId)
                .execute()
            return true
        }
        return false
    }

    override fun deleteUser(userId: Int) {
        handle.createUpdate("delete from _USER where id = :id")
            .bind("id", userId)
            .execute()
    }

    override fun isUserStoredById(userId: Int): Boolean {
        return handle.createQuery("select count(*) from _USER where id = :id")
            .bind("id", userId)
            .mapTo<Int>()
            .single() != 0
    }

    override fun deleteToken(userId: Int) {
        handle.createUpdate("delete from TOKEN where user_id = :user_id")
            .bind("user_id", userId)
            .execute()
    }

    override fun isInQueue(userId: Int): Boolean {
        return handle.createQuery("select count(*) from USER_QUEUE where _user = :_user")
            .bind("_user", userId)
            .mapTo<Int>()
            .single() != 0
    }
}
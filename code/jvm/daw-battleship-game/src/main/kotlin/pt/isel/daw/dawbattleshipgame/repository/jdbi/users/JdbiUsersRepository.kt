package pt.isel.daw.dawbattleshipgame.repository.jdbi.users

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.*
import pt.isel.daw.dawbattleshipgame.repository.UsersRepository
class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from _USER where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserById(id: Int): UserRanking? =
        handle.createQuery("select id, username, wins, gamesPlayed from _USER where id = :id")
                .bind("id", id)
                .mapTo<UserRanking>()
                .singleOrNull()

    override fun getAllUsers(): List<UserInfo> {
        return handle.createQuery("select * from _USER")
                .mapTo<User>()
                .map { it.toUserInfo() }
                .list()
    }

    override fun clearAll() {
        handle.createUpdate("""
            delete from token;
            delete from user_queue;
            delete from _user;
        """.trimIndent()).execute()
    }

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

    override fun getUsersRanking(page: Int, pageSize: Int): List<UserRanking> {
        return handle.createQuery("select id, username, wins, gamesPlayed from _USER order by wins desc limit :pageSize offset :offset")
                .bind("pageSize", pageSize)
                .bind("offset", (page - 1) * pageSize)
                .mapTo<UserRanking>()
                .list()
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

    override fun getFirstUserWithSameConfigInQueue(config: Configuration): Int? {
        return handle.createQuery("select _user from USER_QUEUE where config_hash = :hash order by priority limit 1")
                .bind("hash", config.hashCode())
                .mapTo<Int>()
                .firstOrNull()
    }

    override fun getConfigFromUserQueue(userId: Int): Configuration? {
        val jsonConfig = handle.createQuery("select config from USER_QUEUE where _user = :userid")
                .bind("userid", userId)
                .mapTo<String>()
                .firstOrNull() ?: return null
        // println(jsonConfig)
        return Configuration.mapper.readValue(jsonConfig, Configuration::class.java)
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

    override fun insertInGameQueue(userId: Int, config : Configuration): Boolean {
        if (!isAlreadyInQueue(userId)) {
            handle.createUpdate("insert into USER_QUEUE(_user, config_hash, config) values (:_user, :config_hash, :config)")
                .bind("_user", userId)
                .bind("config_hash", config.hashCode().also { println(it) }) //will generate a hash that is equal if two configs are equal
                .bind("config", Configuration.mapper.writeValueAsString(config))
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
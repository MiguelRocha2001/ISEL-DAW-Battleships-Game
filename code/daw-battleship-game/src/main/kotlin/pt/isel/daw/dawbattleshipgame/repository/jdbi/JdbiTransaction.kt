package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.Transaction
import pt.isel.daw.dawbattleshipgame.repository.UsersRepository
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.repository.jdbi.users.JdbiUsersRepository


class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle) }

    override val gamesRepository: GamesRepository by lazy { JdbiGamesRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}
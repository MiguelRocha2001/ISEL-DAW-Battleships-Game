package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.dawbattleshipgame.utils.testWithHandleAndRollback
import pt.isel.daw.dawbattleshipgame.utils.testWithTransactionManager

internal fun resetDatabase() {
    testWithTransactionManager { transactionManager ->
        transactionManager.run { transaction ->
            transaction.gamesRepository.emptyDatabase()
        }
    }
}
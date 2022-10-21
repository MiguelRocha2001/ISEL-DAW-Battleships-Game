package pt.isel.daw.dawbattleshipgame.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.dawbattleshipgame.repository.Transaction
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiTransaction
import pt.isel.daw.dawbattleshipgame.repository.jdbi.configure

// get from env
private val url = System.getenv("DB_POSTGRES_CONNECTION")

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(url)
    }
).configure()

fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
    block(handle)
    handle.rollback()
}

fun testWithTransactionManager(block: (TransactionManager) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }
    block(transactionManager)
}

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }
    block(transactionManager)
    handle.rollback()
}
package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.tictactow.repository.Transaction

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
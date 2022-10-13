package pt.isel.daw.tictactow.repository

import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.UsersRepository

interface Transaction {
    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository

    fun rollback()
}
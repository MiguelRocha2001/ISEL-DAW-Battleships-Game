package pt.isel.daw.dawbattleshipgame.repository

interface Transaction {
    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository

    fun rollback()
    fun commit()
}
package pt.isel.daw.dawbattleshipgame.services


import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository

class UserServices(private val jdbiGamesRepository: JdbiGamesRepository) {
    private fun createUser(username: String, password: String) {
        jdbiGamesRepository.createUser(username, password)
    }

    private fun login(username: String, password: String): Boolean {
        return jdbiGamesRepository.login(username, password)
    }
}
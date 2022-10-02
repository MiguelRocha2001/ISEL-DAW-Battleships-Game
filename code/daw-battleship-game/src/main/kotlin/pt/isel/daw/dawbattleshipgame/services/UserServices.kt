package pt.isel.daw.dawbattleshipgame.services


import pt.isel.daw.dawbattleshipgame.data.UserData

class UserServices(private val data: UserData) {
    private fun createUser(username: String, password: String) {
        data.createUser(username, password)
    }
}
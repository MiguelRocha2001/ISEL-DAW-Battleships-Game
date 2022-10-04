package pt.isel.daw.dawbattleshipgame.services


import pt.isel.daw.dawbattleshipgame.data.DataBase

class UserServices(private val dataBase: DataBase) {
    private fun createUser(username: String, password: String) {
        dataBase.createUser(username, password)
    }

    private fun login(username: String, password: String): Boolean {
        return dataBase.login(username, password)
    }
}
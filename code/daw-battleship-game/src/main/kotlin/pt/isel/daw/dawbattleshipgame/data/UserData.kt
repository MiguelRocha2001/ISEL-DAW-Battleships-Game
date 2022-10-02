package pt.isel.daw.dawbattleshipgame.data

class UserData {
    private val dataBase = DataBase()

    fun createUser(username: String, password: String) {
        dataBase.createUser(username, password)
    }
}
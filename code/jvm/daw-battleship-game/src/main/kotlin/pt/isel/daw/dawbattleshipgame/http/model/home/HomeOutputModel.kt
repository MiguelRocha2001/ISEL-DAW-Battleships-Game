package pt.isel.daw.dawbattleshipgame.http.model.home

data class HomeOutputModel(
    val title: String,
)

data class ServerInfoOutputModel(
    val authors: List<Pair<String, String>>,
    val systemVersion: String
)
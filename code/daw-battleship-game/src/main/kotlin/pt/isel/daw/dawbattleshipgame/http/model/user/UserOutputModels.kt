package pt.isel.daw.dawbattleshipgame.http.model.user

import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction

class UserTokenOutputModel(
    val properties: List<Pair<String, String>>,
    val actions: List<SirenAction>,
)
class UserHomeOutputModel(
    val id: String,
    val username: String,
)
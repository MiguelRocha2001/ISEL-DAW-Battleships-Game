package pt.isel.daw.dawbattleshipgame.http.model.user

import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction

class UserTokenOutputModelSiren(
    val properties: TokenOutputModel,
    val actions: List<SirenAction>,
)
data class TokenOutputModel(val token: String)

class UserHomeOutputModel(
    val id: String,
    val username: String,
)
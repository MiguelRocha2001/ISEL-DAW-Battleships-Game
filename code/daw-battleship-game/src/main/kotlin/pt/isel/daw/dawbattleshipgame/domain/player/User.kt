package pt.isel.daw.dawbattleshipgame.domain.player

data class User(
    val id: String,
    val username: String,
    val hashedPassword: String,
)
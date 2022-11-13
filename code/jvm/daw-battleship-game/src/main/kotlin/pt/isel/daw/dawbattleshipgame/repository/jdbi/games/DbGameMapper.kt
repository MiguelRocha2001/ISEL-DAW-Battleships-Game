package pt.isel.daw.dawbattleshipgame.repository.jdbi.games


data class DbGameMapper(
    val id: Int,
    val state: String,
    val player1: Int,
    val player2: Int,
    val created: Long,
    val updated: Long,
    val deadline: Long,
    val winner: Int?,
    val player_turn: Int?
    )

data class DbBoardMapper(val game: Int, val _user: Int, val confirmed: Boolean, val grid : String)

data class DbConfigurationMapper(val game: Int, val board_size: Int, val n_shots: Int, val timeout: Long)

data class DbShipMapper(val configuration: Int, val name: String, val length: Int)
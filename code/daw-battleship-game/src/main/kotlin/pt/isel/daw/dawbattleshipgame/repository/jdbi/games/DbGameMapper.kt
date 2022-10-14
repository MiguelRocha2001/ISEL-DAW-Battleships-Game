package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

data class DbGameMapper(val id: Int, val user1: Int, val user2: Int, val finished: Boolean, val player_turn: Int?)

data class DbBoardMapper(val game: Int, val _user: Int, val confirmed: Boolean)

data class DbPanelMapper(val game: Int, val _user: Int, val idx: Int, val is_hit: Boolean, val type: String)

data class DbConfigurationMapper(val game: Int, val board_size: Int, val n_shots: Int, val timeout: Int)

data class DbShipMapper(val configuration: Int, val name: String, val length: Int)
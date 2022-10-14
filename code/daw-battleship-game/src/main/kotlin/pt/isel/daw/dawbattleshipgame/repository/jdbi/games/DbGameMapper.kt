package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import pt.isel.daw.dawbattleshipgame.domain.ship.toShipTypeOrNull


data class DbGameMapper(val id: Int, val user1: Int, val user2: Int, val finished: Boolean, val player_turn: Int?)

data class DbBoardMapper(val game: Int, val _user: Int, val confirmed: Boolean)

data class DbPanelMapper(val game: Int, val _user: Int, val x: Int, val y:Int, val isHit : Boolean, val type : String) {
    fun toPanel() = Panel(Coordinate(x,y),type.toShipTypeOrNull(), isHit)
}

data class DbConfigurationMapper(val game: Int, val board_size: Int, val n_shots: Int, val timeout: Int)

data class DbShipMapper(val configuration: Int, val name: String, val length: Int)
package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType


data class DbGameMapper(
    val id: Int,
    val state: String,
    val player1: Int,
    val player2: Int,
    val created: Long, //bigint
    val updated: Long,
    val deadline: Long,
    val winner: Int?,
    val player_turn: Int?
    )

data class DbBoardMapper(val game: Int, val _user: Int, val confirmed: Boolean, val grid : String)

data class DbConfigurationMapper(
        val game: Int,
        val board_size: Int,
        val n_shots: Long,
        val fleet : String,
        val timeout: Long
        ){
    fun toConfiguration(): Configuration {
        val typeRef = object : TypeReference<Map<ShipType, Int>>() {}
        return Configuration(
                board_size,
                ObjectMapper().readValue(fleet, typeRef),
                n_shots,
                timeout
        )
    }
}

data class DbShipMapper(val configuration: Int, val name: String, val length: Int)
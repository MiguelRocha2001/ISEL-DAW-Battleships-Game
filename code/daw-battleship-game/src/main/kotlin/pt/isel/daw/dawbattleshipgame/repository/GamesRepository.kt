package pt.isel.daw.dawbattleshipgame.repository

import pt.isel.daw.dawbattleshipgame.domain.game.Game
import java.util.UUID

interface GamesRepository {
    fun insert(game: Game)
    fun getById(id: UUID): Game?
    fun update(game: Game)
}
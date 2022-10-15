package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.game.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.domain.game.SinglePhase
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.*
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertBoard
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertBoards
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertGame

/*
sealed class DbGameResponse
data class DbPreparationPhase(val preparationPhase: PreparationPhase) : DbGameResponse()
data class DbWaitingPhase(val waitingPhase: WaitingPhase) : DbGameResponse()
data class DbPlayerPreparationPhase(val playerPreparationPhase: PlayerPreparationPhase) : DbGameResponse()
data class DbBattlePhase(val game: BattlePhase) : DbGameResponse()
 */

class JdbiGamesRepository(
    private val handle: Handle,
): GamesRepository {

    override fun getGame(gameId: Int): Game? {
        return fetchGameInternal(handle, gameId)
    }

    override fun getGameByUser(userId: Int): Game? {
        return fetchGameByUser(handle, userId)
    }

    override fun saveGame(game: Game) {
        deleteGame(handle, game.gameId)
        insertGame(handle, game)
        insertBoards(handle, game)
        insertConfiguration(handle, game.gameId, game.configuration)
    }

    override fun savePreparationPhase(singlePhase: SinglePhase) {
        saveGame(singlePhase)
    }

    override fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase) {
        insertBoard(handle, playerPreparationPhase.gameId, playerPreparationPhase.playerId, playerPreparationPhase.board)
    }

    override fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase) {
        confirmBoard(handle, playerWaitingPhase.gameId, playerWaitingPhase.playerId)
    }

    override fun getPreparationPhase(gameId: Int): SinglePhase? {
        TODO("Not yet implemented")
    }


    override fun createUser(username: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun saveConfiguration(configuration: Configuration) {
        TODO("Not yet implemented")
    }

    override fun login(username: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWaitingUser(configuration: Configuration): Int? {
        TODO("Not yet implemented")
    }

    override fun joinGameQueue(userId: Int, configuration: Configuration) {
        TODO("Not yet implemented")
    }

    override fun removeUserFromQueue(userWating: Int) {
        TODO("Not yet implemented")
    }

    override fun removeGame(gameId: Int) {
        TODO("Not yet implemented")
    }

    override fun emptyRepository() {
        clearAllTables(handle)
    }
}
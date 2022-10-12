package pt.isel.daw.dawbattleshipgame.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.*
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertBoard
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertBoards
import pt.isel.daw.dawbattleshipgame.repository.jdbi.games.insertGame

sealed class DbGameResponse
data class DbPreparationPhase(val preparationPhase: PreparationPhase) : DbGameResponse()
data class DbWaitingPhase(val waitingPhase: WaitingPhase) : DbGameResponse()
data class DbPlayerPreparationPhase(val playerPreparationPhase: PlayerPreparationPhase) : DbGameResponse()
data class DbBattlePhase(val game: BattlePhase) : DbGameResponse()

class JdbiGamesRepository(
    private val handle: Handle,
) {
    internal fun saveGame(game: Game) {
        deleteGame(handle, game.gameId)
        insertGame(handle, game)
        insertBoards(handle, game)
        insertConfiguration(handle, game.gameId, game.configuration)
    }

    internal fun savePreparationPhase(preparationPhase: PreparationPhase) {
        saveGame(preparationPhase)
    }

    internal fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase) {
        insertBoard(handle, playerPreparationPhase.gameId, playerPreparationPhase.playerId, playerPreparationPhase.board)
    }

    internal fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase) {
        confirmBoard(handle, playerWaitingPhase.gameId, playerWaitingPhase.playerId)
    }

    internal fun getPreparationPhase(gameId: Int): PreparationPhase? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the game if both players have confirmed their fleets.
     */
    internal fun getWaitingPhase(gameId: Int): DbWaitingPhase? {
        TODO("Not yet implemented")
    }

    internal fun getPlayerPreparationPhase(token: String): DbPlayerPreparationPhase? {
        TODO("Not yet implemented")
    }

    internal fun getGame(): DbGameResponse? {
        TODO("Not yet implemented")
    }


    fun createUser(username: String, password: String) {
        TODO("Not yet implemented")
    }

    fun saveConfiguration(configuration: Configuration) {
        TODO("Not yet implemented")
    }

    fun login(username: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    fun getWaitingUser(configuration: Configuration): String? {
        TODO("Not yet implemented")
    }

    fun joinGameQueue(token: String, configuration: Configuration) {
        TODO("Not yet implemented")
    }

    fun removeUserFromQueue(userWating: String) {
        TODO("Not yet implemented")
    }

    fun removeGame(gameId: Int) {
        TODO("Not yet implemented")
    }
}
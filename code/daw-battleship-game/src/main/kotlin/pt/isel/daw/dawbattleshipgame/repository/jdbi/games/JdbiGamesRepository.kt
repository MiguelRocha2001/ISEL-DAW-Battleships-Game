package pt.isel.daw.dawbattleshipgame.repository.jdbi.games

import org.jdbi.v3.core.Handle
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase
import pt.isel.daw.dawbattleshipgame.repository.GamesRepository


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

    override fun removeGame(gameId: Int) {
        deleteGame(handle, gameId)
    }

    override fun emptyRepository() {
        clearAllTables(handle)
    }
}
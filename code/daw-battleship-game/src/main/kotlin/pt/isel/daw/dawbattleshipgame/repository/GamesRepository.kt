package pt.isel.daw.dawbattleshipgame.repository

<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerWaitingPhase

interface GamesRepository {
    fun saveGame(game: Game)

    fun savePreparationPhase(singlePhase: SinglePhase)

    fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPreparationPhase)
    fun savePlayerWaitingPhase(playerWaitingPhase: PlayerWaitingPhase)
=======
<<<<<<< Updated upstream
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import java.util.UUID

interface GamesRepository {
    fun insert(game: Game)
    fun getById(id: UUID): Game?
    fun update(game: Game)
=======
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.SinglePhase
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase


interface GamesRepository {
    fun createGame(configuration: Configuration, player1: Int, player2: Int) : Int?
    fun saveGame(game: Game)
    fun savePreparationPhase(singlePhase: SinglePhase)
    fun savePlayerPreparationPhase(playerPreparationPhase: PlayerPhase)
    fun savePlayerWaitingPhase(playerWaitingPhase: PlayerPhase)
>>>>>>> Stashed changes
    fun getPreparationPhase(gameId: Int): SinglePhase?
    fun createUser(username: String, password: String)
    fun saveConfiguration(configuration: Configuration)
    fun login(username: String, password: String): Boolean
    fun removeGame(gameId: Int)
    fun emptyRepository()
    fun getGame(gameId: Int): Game?
    fun getGameByUser(userId: Int): Game?
<<<<<<< Updated upstream
=======
    fun getGameIdByUser(userId : Int) : Int?
>>>>>>> Stashed changes
>>>>>>> Stashed changes
}
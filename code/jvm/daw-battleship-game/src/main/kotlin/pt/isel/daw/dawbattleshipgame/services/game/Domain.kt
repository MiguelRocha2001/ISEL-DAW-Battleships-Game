package pt.isel.daw.dawbattleshipgame.services.game

import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.GameState


sealed class GameCreationError: Error() {
    object GameNotFound: GameCreationError()
    object UserAlreadyInGame : GameCreationError()
    object UserAlreadyInQueue : GameCreationError()
}
typealias GameCreationResult = Either<GameCreationError, Pair<GameState, Int?>>

sealed class PlaceShipsError : Error() {
    object GameNotFound: PlaceShipsError()
    object ActionNotPermitted : PlaceShipsError()
    object InvalidMove: PlaceShipsError()
}
typealias PlaceShipsResult = Either<PlaceShipsError, Unit>

sealed class UpdateShipError : Error(){
    object GameNotFound: UpdateShipError()
    object ActionNotPermitted : UpdateShipError()
    object InvalidMove: UpdateShipError()
}
typealias UpdateShipResult = Either<UpdateShipError, Unit>

sealed class FleetConfirmationError : Error(){
    object GameNotFound: FleetConfirmationError()
    object ActionNotPermitted : FleetConfirmationError()
}
typealias FleetConfirmationResult = Either<FleetConfirmationError, Unit>

sealed class PlaceShotError : Error(){
    object GameNotFound : PlaceShotError()
    object ActionNotPermitted : PlaceShotError()
    object InvalidMove: PlaceShotError()
}
typealias PlaceShotResult = Either<PlaceShotError, Unit>

sealed class GameSearchError : Error(){
    object GameNotFound: GameSearchError()
}
typealias BoardResult = Either<GameSearchError, Board>

sealed class GameStateError : Error(){
    object GameNotFound: GameStateError()
}
typealias GameStateResult = Either<GameStateError, GameState>

sealed class GameIdError : Error(){
    object GameNotFound: GameIdError()
    object UserInGameQueue: GameIdError()
}
typealias GameIdResult = Either<GameIdError, Int>

sealed class GameError : Error(){
    object GameNotFound: GameError()
}
typealias GameResult = Either<GameError, Game>

sealed class DeleteGameError: Error() {
    object GameNotFound: DeleteGameError()
}
typealias DeleteGameResult = Either<DeleteGameError, Unit>

package pt.isel.daw.dawbattleshipgame.services.game

import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.state.GameState

sealed class GameCreationError: Error() {
    object GameNotFound: GameCreationError()
    object UserAlreadyInGame : GameCreationError()
    object UserAlreadyInQueue : GameCreationError()
}
typealias GameCreationResult = Either<GameCreationError, Pair<GameState, Int?>>

sealed class PlaceShipError {
    object GameNotFound: PlaceShipError()
    object ActionNotPermitted : PlaceShipError()
    object InvalidMove: PlaceShipError()
}
typealias PlaceShipResult = Either<PlaceShipError, GameState>

sealed class MoveShipError {
    object GameNotFound: MoveShipError()
    object ActionNotPermitted : MoveShipError()
    object InvalidMove: MoveShipError()
}
typealias MoveShipResult = Either<MoveShipError, GameState>

sealed class RotateShipError {
    object GameNotFound: RotateShipError()
    object ActionNotPermitted : RotateShipError()
    object InvalidMove: RotateShipError()
}
typealias RotateShipResult = Either<RotateShipError, GameState>

sealed class FleetConfirmationError {
    object GameNotFound: FleetConfirmationError()
    object ActionNotPermitted : FleetConfirmationError()
}
typealias FleetConfirmationResult = Either<FleetConfirmationError, GameState>

sealed class PlaceShotError {
    object GameNotFound: PlaceShotError()
    object ActionNotPermitted : PlaceShotError()
    object InvalidMove: PlaceShotError()
}
typealias PlaceShotResult = Either<PlaceShotError, GameState>

sealed class GameSearchError {
    object GameNotFound: GameSearchError()
}
typealias BoardResult = Either<GameSearchError, Board>

sealed class GameStateError {
    object GameNotFound: GameStateError()
}
typealias GameStateResult = Either<GameStateError, GameState>

sealed class GameIdError {
    object GameNotFound: GameIdError()
}
typealias GameIdResult = Either<GameIdError, Int>

sealed class GameError {
    object GameNotFound: GameError()
}
typealias GameResult = Either<GameError, Game>

sealed class DeleteGameError {
    object GameNotFound: DeleteGameError()
}
typealias DeleteGameResult = Either<DeleteGameError, Unit>

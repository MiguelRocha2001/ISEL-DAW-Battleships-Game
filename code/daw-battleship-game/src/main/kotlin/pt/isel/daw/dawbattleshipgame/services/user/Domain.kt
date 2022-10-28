package pt.isel.daw.dawbattleshipgame.services.user

import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
}
typealias UserCreationResult = Either<UserCreationError, Int>


sealed class UserRankingError {
    object UnableToGetUserRanking : UserRankingError()
}
typealias UserRankingResult = Either<UserRankingError, List<UserRanking>>

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, String>
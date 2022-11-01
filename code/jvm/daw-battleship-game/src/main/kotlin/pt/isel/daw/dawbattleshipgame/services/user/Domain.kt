package pt.isel.daw.dawbattleshipgame.services.user

import pt.isel.daw.dawbattleshipgame.Either

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
}
typealias UserCreationResult = Either<UserCreationError, Int>

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, String>

sealed class UserDeletionError {
    object UserDoesNotExist : UserDeletionError()
}
typealias UserDeletionResult = Either<UserDeletionError, Unit>
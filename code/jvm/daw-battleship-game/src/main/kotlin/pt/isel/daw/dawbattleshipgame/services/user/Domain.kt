package pt.isel.daw.dawbattleshipgame.services.user

import pt.isel.daw.dawbattleshipgame.Either

sealed class UserCreationError : Error() {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
    object InvalidUsername: UserCreationError()

}
typealias UserCreationResult = Either<UserCreationError, Int>

sealed class TokenCreationError : Error() {
    object UserOrPasswordAreInvalid : TokenCreationError()
}
typealias TokenCreationResult = Either<TokenCreationError, String>

sealed class UserDeletionError : Error() {
    object UserDoesNotExist : UserDeletionError()
}
typealias UserDeletionResult = Either<UserDeletionError, Unit>
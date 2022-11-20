package pt.isel.daw.dawbattleshipgame.http.model

import org.springframework.http.ResponseEntity
import pt.isel.daw.dawbattleshipgame.Either

val domainProblemMapper = fun(error : Error) =
        problems[error::class.simpleName] as ResponseEntity<*>

fun <L,R> Either<L, R>.map (f : (R) -> ResponseEntity<*>) : ResponseEntity<*> {
    return when(this) {
        is Either.Right -> f(this.value)
        is Either.Left -> domainProblemMapper(this.value as Error)
    }
}
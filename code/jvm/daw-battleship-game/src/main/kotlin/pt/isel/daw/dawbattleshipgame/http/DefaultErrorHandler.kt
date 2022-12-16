package pt.isel.daw.dawbattleshipgame.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import pt.isel.daw.dawbattleshipgame.http.model.Problem

@ControllerAdvice
class DefaultErrorHandler {
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(ex: Exception): ResponseEntity<Problem> {
        return Problem.response(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), Problem.userNotFound
        )
    }
}
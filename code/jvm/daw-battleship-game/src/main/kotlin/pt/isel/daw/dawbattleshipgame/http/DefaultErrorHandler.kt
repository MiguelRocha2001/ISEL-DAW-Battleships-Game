package pt.isel.daw.dawbattleshipgame.http

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import pt.isel.daw.dawbattleshipgame.http.model.Problem
private val logger: Logger = LoggerFactory.getLogger("ExceptionHandler")

@ControllerAdvice
class DefaultErrorHandler {
    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(ex: Exception): ResponseEntity<Problem> {
        return when(ex) {
            is ApiException -> Problem.response(ex.status, Problem(
                        ex.type, ex.title, ex.detail
                    ))
            else -> Problem.response(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), Problem.errorHasOccurred
            )
        }.also { logger.error(ex.message) }
    }
}
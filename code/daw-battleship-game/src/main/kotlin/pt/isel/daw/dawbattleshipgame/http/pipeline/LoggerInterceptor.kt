package pt.isel.daw.dawbattleshipgame.http.pipeline

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.daw.dawbattleshipgame.domain.player.User

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggerInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            logger.info("Request: ${request.method} ${request.requestURI}")
        }
        return true
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LoggerInterceptor::class.java)
    }
}
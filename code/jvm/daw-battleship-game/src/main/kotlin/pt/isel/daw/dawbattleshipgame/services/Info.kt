package pt.isel.daw.dawbattleshipgame.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

data class ServerInfo(
    val authors: List<Pair<String, String>> = listOf(
        "António Carvalho" to "A48347@alunos.isel.pt",
        "Pedro Silva" to "A47128@alunos.isel.pt",
        "Miguel Rocha" to "A47185@alunos.isel.pt"
    ),
    val systemVersion: String = "1.0.0",
)

@Component
class InfoServices {
    private val logger: Logger = LoggerFactory.getLogger("InfoServices")
    fun getServerInfo(): ServerInfo = ServerInfo().also { logger.info("Server info retrieved") }
}
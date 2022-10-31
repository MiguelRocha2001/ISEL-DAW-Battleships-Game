package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.TransactionManager

data class ServerInfo(
    val authors: List<Pair<String, String>> = listOf(
        "António Carvalho" to "A48347@alunos.isel.pt",
        "Pedro Silva" to "",
        "Miguel Rocha" to "A47185@alunos.isel.pt"
    ),
    val systemVersion: String = "1.0.0",
)

@Component
class InfoServices(
    private val transactionManager: TransactionManager,
) {
    fun getServerInfo(): ServerInfo = ServerInfo()
}
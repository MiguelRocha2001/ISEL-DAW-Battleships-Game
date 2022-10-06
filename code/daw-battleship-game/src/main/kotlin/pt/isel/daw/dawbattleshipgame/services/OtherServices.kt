package pt.isel.daw.dawbattleshipgame.services

import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository


@Component
class OtherServices(private val jdbiGamesRepository: JdbiGamesRepository) {

}
package pt.isel.daw.dawbattleshipgame.services


import org.springframework.stereotype.Component
import pt.isel.daw.dawbattleshipgame.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.dawbattleshipgame.domain.Configuration

@Component
class GameConfigurationServices(private val jdbiGamesRepository: JdbiGamesRepository) {
    fun setConfiguration(configuration: Configuration) {
        jdbiGamesRepository.saveConfiguration(configuration)
    }
}
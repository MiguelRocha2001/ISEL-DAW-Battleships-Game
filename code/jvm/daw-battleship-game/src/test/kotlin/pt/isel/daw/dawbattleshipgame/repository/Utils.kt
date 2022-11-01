package pt.isel.daw.dawbattleshipgame.repository

internal fun resetGamesDatabase(gamesRepository: GamesRepository) {
    gamesRepository.emptyRepository()
}
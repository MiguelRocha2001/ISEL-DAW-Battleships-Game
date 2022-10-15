package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.game.single.Single

data class SinglePhase(
    override val gameId: Int,
    override val configuration: Configuration,
    override val player1: Int,
    override val player2: Int,
    val player1Game: Single,
    val player2Game: Single
) : Game()
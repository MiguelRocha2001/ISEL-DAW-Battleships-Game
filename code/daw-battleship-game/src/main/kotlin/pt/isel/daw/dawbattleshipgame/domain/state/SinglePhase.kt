package pt.isel.daw.dawbattleshipgame.domain.state

import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPhase

data class SinglePhase(
    override val gameId: Int,
    override val configuration: Configuration,
    override val player1: Int,
    override val player2: Int,
    val player1Game: PlayerPhase,
    val player2Game: PlayerPhase,
) : Game()
package pt.isel.daw.dawbattleshipgame.domain.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.Game

class EndPhase(override val gameId: Int, override val configuration: Configuration) : Game()
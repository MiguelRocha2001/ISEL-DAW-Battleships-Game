package pt.isel.daw.dawbattleshipgame.http.model.game

import pt.isel.daw.dawbattleshipgame.domain.board.Board

data class BoardOutputModel(val board: Board)

data class GameIdOutputModel(val id: Int)
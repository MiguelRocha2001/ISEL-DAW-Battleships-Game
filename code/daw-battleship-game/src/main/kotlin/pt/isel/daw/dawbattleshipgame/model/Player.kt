package pt.isel.daw.dawbattleshipgame.model

enum class Player {
    Player1, Player2;

    fun getOpponent() = if (this == Player1) Player2 else Player1
}
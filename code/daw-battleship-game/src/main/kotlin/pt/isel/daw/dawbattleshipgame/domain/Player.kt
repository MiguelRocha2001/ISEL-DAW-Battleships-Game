package pt.isel.daw.dawbattleshipgame.domain

enum class Player {
    Player1, Player2;

    fun other() = if (this == Player1) Player2 else Player1
}
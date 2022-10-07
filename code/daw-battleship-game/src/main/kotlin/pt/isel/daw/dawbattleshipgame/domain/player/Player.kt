package pt.isel.daw.dawbattleshipgame.domain.player

enum class Player {
    Player1, Player2;
    fun other() = if (this == Player1) Player2 else Player1
}
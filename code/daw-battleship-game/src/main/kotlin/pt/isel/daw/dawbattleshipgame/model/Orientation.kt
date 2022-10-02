package pt.isel.daw.dawbattleshipgame.model

enum class Orientation {
    HORIZONTAL,
    VERTICAL;

    fun other() = if (this === HORIZONTAL) VERTICAL else HORIZONTAL
    fun isVertical() = this == VERTICAL
    fun isHorizontal() = this == HORIZONTAL
}
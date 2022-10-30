package pt.isel.daw.dawbattleshipgame.domain.ship

enum class Orientation {
    HORIZONTAL,
    VERTICAL;

    companion object{
        fun random() = Orientation.values().random()
    }
    fun other() = if (this === HORIZONTAL) VERTICAL else HORIZONTAL
    fun isVertical() = this == VERTICAL
    fun isHorizontal() = this == HORIZONTAL
}
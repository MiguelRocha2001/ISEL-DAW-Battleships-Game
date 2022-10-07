package pt.isel.daw.dawbattleshipgame.domain.ship

enum class Orientation {
    HORIZONTAL,
    VERTICAL;

    companion object{
        fun random() = Orientation.values()[
                (0 until Orientation.values().size).random()
        ]
    }

    fun other() = if (this === HORIZONTAL) VERTICAL else HORIZONTAL
    fun isVertical() = this == VERTICAL
    fun isHorizontal() = this == HORIZONTAL
}
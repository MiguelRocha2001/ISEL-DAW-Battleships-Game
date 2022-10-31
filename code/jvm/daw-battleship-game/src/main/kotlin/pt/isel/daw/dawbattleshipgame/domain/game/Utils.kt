package pt.isel.daw.dawbattleshipgame.domain.game


fun requireNull(value: Int?) {
    require(value == null) {
        "Illegal game state"
    }
}

fun requireNotNull(value: Int?) {
    require(value != null) {
        "Illegal game state"
    }
}

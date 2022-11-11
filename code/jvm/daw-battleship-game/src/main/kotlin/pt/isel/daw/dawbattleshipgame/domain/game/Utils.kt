package pt.isel.daw.dawbattleshipgame.domain.game


fun <T> checkNull(value: T?) {
    require(value == null) {
        "Illegal game state"
    }
}

fun <T> checkNotNull(value: T?) {
    require(value != null) {
        "Illegal game state"
    }
}

package pt.isel.daw.dawbattleshipgame.domain.ship

enum class ShipType {
    CARRIER,
    BATTLESHIP,
    CRUISER,
    SUBMARINE,
    DESTROYER;
}

fun String.toShipType(): ShipType {
    return when (this.lowercase()) {
        "carrier" -> ShipType.CARRIER
        "battleship" -> ShipType.BATTLESHIP
        "cruiser" -> ShipType.CRUISER
        "submarine" -> ShipType.SUBMARINE
        "destroyer" -> ShipType.DESTROYER
        else -> throw IllegalArgumentException("Invalid ship type")
    }
}

fun String.toShipTypeOrNull(): ShipType? {
    return when (this.lowercase()) {
        "carrier" -> ShipType.CARRIER
        "battleship" -> ShipType.BATTLESHIP
        "cruiser" -> ShipType.CRUISER
        "submarine" -> ShipType.SUBMARINE
        "destroyer" -> ShipType.DESTROYER
        else -> null
    }
}
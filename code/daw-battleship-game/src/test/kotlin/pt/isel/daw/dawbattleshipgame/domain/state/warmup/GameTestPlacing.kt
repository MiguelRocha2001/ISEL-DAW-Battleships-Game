package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
import pt.isel.daw.dawbattleshipgame.domain.game.GameLogic
=======
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt


class GameTestPlacing {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun initializing_new_game() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_1() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
        val gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] | [] |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_2() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A2".toCoordinate(), Orientation.HORIZONTAL)
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A2".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A2".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
    fun placing_ship_on_valid_location_3() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "E8".toCoordinate(), Orientation.HORIZONTAL)
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
        val gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "E8".toCoordinate(), Orientation.HORIZONTAL)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "E8".toCoordinate(), Orientation.HORIZONTAL)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    | [] | [] |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }


    @Test
    fun placing_ship_on_invalid_location_1() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "j1".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
        assertThrows<Exception> {
            GameLogic.placeShip(game, ShipType.DESTROYER, "j1".toCoordinate(), Orientation.HORIZONTAL)
        }
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "j1".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
    }

    @Test
    fun placing_ship_on_invalid_location_2() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        val gameResult = game.tryPlaceShip(ShipType.DESTROYER, "J10".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
        val game = Game.newGame(gameConfig)
        assertThrows<Exception> {
            GameLogic.placeShip(game, ShipType.DESTROYER, "J10".toCoordinate(), Orientation.HORIZONTAL)
        }
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game 
        val gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "J10".toCoordinate(), Orientation.HORIZONTAL)
        assertEquals(null, gameResult)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestPlacing.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestPlacing.kt
    }
}
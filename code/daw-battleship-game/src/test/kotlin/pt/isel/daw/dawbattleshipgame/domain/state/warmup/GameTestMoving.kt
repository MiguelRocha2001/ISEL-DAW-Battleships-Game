package pt.isel.daw.dawbattleshipgame.domain.state.warmup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.dawbattleshipgame.domain.state.utils.getGameTestConfiguration
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.toCoordinate
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
import pt.isel.daw.dawbattleshipgame.domain.state.single.PlayerPreparationPhase
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
import pt.isel.daw.dawbattleshipgame.domain.game.GameLogic
=======
import pt.isel.daw.dawbattleshipgame.utils.generateRandomId
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt


class GameTestMoving {
    private val gameId = generateRandomId()
    private val player1 = generateRandomId()
    private val player2 = generateRandomId()
    private val configuration = getGameTestConfiguration()

    @Test
    fun valid_ship_move_1() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A1".toCoordinate(), "B4".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = GameLogic.moveShip(gameResult, "A1".toCoordinate(), "B4".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A1".toCoordinate(), "B4".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    | [] | [] |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_2() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("B1".toCoordinate(), "B4".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = GameLogic.moveShip(gameResult, "B1".toCoordinate(), "B4".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("B1".toCoordinate(), "B4".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 | [] | [] |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_3() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("B1".toCoordinate(), "J1".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = GameLogic.moveShip(gameResult, "B1".toCoordinate(), "J1".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("B1".toCoordinate(), "J1".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    | [] | [] |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_4() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A1".toCoordinate(), "A1".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = GameLogic.moveShip(gameResult, "A1".toCoordinate(), "A1".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A1".toCoordinate(), "A1".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_5() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("B1".toCoordinate(), "B1".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = GameLogic.moveShip(gameResult, "B1".toCoordinate(), "B1".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("B1".toCoordinate(), "B1".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 | [] | [] |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
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
    fun valid_ship_move_6() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.tryMoveShip("D2".toCoordinate(), "D6".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = GameLogic.moveShip(gameResult, "D2".toCoordinate(), "D6".toCoordinate())
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.SUBMARINE, "D1".toCoordinate(), Orientation.VERTICAL)
        gameResult = gameResult?.logic?.tryMoveShip("D2".toCoordinate(), "D6".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    | [] |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult.toString()
        )
    }

    @Test
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
    fun invalid_ship_move_1() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("B1".toCoordinate(), "A1".toCoordinate())
=======
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
    fun invalid_ship_move() {
        val game = Game.newGame(gameConfig)
        var gameResult = GameLogic.placeShip(game, ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        assertThrows<Exception> {
            gameResult = GameLogic.moveShip(gameResult, "B1".toCoordinate(), "A1".toCoordinate())
        }
=======
    fun invalid_ship_move_1() {
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("B1".toCoordinate(), "A1".toCoordinate())
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
        assertEquals(null, gameResult)
    }

    @Test
    fun invalid_ship_move_2() {
<<<<<<< Updated upstream:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game as PlayerPreparationPhase
        var gameResult = game.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.tryMoveShip("A1".toCoordinate(), "J8".toCoordinate())
        assertEquals(null, gameResult)
=======
        val game = Game.newGame(gameId, player1, player2, configuration).player1Game
        var gameResult = game.logic.tryPlaceShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.logic?.tryMoveShip("A1".toCoordinate(), "J8".toCoordinate())
        assertEquals(null, gameResult)
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/state/warmup/GameTestMoving.kt
>>>>>>> Stashed changes:code/daw-battleship-game/src/test/kotlin/pt/isel/daw/dawbattleshipgame/domain/game/warmup/GameTestMoving.kt
    }
}
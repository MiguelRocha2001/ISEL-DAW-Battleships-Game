package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.game.*
import pt.isel.daw.dawbattleshipgame.http.model.user.UserTokenOutputModel
import pt.isel.daw.dawbattleshipgame.services.game.*
import java.net.URI

@RestController
class GamesController(
    private val gameServices: GameServices
) {
    @PostMapping(Uris.GAMES_CREATE)
    fun create(
        user: User,
        @RequestBody createGameInputModel: CreateGameInputModel
    ): ResponseEntity<*> {
        val res = gameServices.startGame(user.id, createGameInputModel.configuration)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameStartOutputModel(
                    SirenAction(
                        name = "get-game-id",
                        title = "Get Game",
                        method = HttpMethod.GET,
                        href = URI(Uris.GAMES_GET_GAME_ID),
                        type = MediaType.APPLICATION_JSON,
                    )
                ))
            is Either.Left -> when (res.value) {
                GameCreationError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                GameCreationError.UserAlreadyInQueue -> Problem.response(405, Problem.toBeChanged)
                GameCreationError.UserAlreadyInGame -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_GAME_ID)
    fun getCurrentGameId(user: User): ResponseEntity<*> {
        val res = gameServices.getGameIdByUser(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameIdOutputModel(
                    properties = listOf(
                        "gameId" to res.value.toString()
                    ),
                    actions = listOf(
                        SirenAction(
                            name = "get-game",
                            title = "Get Game",
                            method = HttpMethod.POST,
                            href = URI(Uris.GAMES_PLACE_SHIP),
                            type = MediaType.APPLICATION_JSON,
                            fields = listOf(
                                SirenAction.Field(
                                    name = "shipType",
                                    type = "text",
                                ),
                                SirenAction.Field(
                                    name = "",
                                    type = "",
                                ),
                                SirenAction.Field(
                                    name = "orientation",
                                    type = "text",
                                )
                            )
                        )
                    )
                ))
            is Either.Left -> when (res.value) {
                GameIdError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_PLACE_SHIP)
    fun placeShip(
        user: User,
        @PathVariable id: Int,
        @RequestBody placeShipInputModel: PlaceShipInputModel
    ): ResponseEntity<*> {
        val res = gameServices.placeShip(
            user.id,
            placeShipInputModel.shipType.toShipType(),
            Coordinate(placeShipInputModel.position.row, placeShipInputModel.position.column),
            placeShipInputModel.orientation.toOrientation()
        )
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                PlaceShipError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                PlaceShipError.ActionNotPermitted -> Problem.response(405, Problem.toBeChanged)
                PlaceShipError.InvalidMove -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_MOVE_SHIP)
    fun moveShip(
        user: User,
        @PathVariable id: Int,
        @RequestBody moveShipInputModel: MoveShipInputModel
    ): ResponseEntity<*> {
        val res = gameServices.moveShip(user.id, moveShipInputModel.origin, moveShipInputModel.destination)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                MoveShipError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                MoveShipError.ActionNotPermitted -> Problem.response(405, Problem.toBeChanged)
                MoveShipError.InvalidMove -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_ROTATE_SHIP)
    fun rotateShip(
        user: User,
        @PathVariable id: Int,
        @RequestBody coordinate: Coordinate
    ): ResponseEntity<*> {
        val res = gameServices.rotateShip(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                RotateShipError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                RotateShipError.ActionNotPermitted -> Problem.response(405, Problem.toBeChanged)
                RotateShipError.InvalidMove -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_CONFIRM_FLEET)
    fun confirmFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.confirmFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                FleetConfirmationError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                FleetConfirmationError.ActionNotPermitted -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_PLACE_SHOT)
    fun placeShot(
        user: User,
        @PathVariable id: Int,
        @RequestBody coordinate: Coordinate
    ): ResponseEntity<*> {
        val res = gameServices.placeShot(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                PlaceShotError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                PlaceShotError.ActionNotPermitted -> Problem.response(405, Problem.toBeChanged)
                PlaceShotError.InvalidMove -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_MY_FLEET)
    fun getMyFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getMyFleetLayout(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(BoardOutputModel(res.value))
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_OPPONENT_FLEET)
    fun getOpponentFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getOpponentFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(BoardOutputModel(res.value))
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }

    @GetMapping(Uris.GAMES_STATE)
    fun getGameState(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getGameState(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameStateOutputModel.get(res.value))
            is Either.Left -> when (res.value) {
                GameStateError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }

    /*
    @GetMapping(Uris.GAMES_STATE)
    fun getGame(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getGame(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameStateOutputModel.get(res.value))
            is Either.Left -> when (res.value) {
                GameStateError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }
     */
}
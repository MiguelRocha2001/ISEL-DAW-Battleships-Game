package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import pt.isel.daw.dawbattleshipgame.http.hypermedia.*
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.game.*
import pt.isel.daw.dawbattleshipgame.services.game.*

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
                    properties = GameProperties(GameStateOutputModel.get(res.value)),
                    links = startGameLinks(user.id),
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

        val te = res
        val test = GameIdOutputSiren(
            properties = GameIdOutputModel((te as Either.Right).value),
            links = gameInfoActions(te.value)
        )
        
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameIdOutputSiren(
                    properties = GameIdOutputModel(res.value),
                    links = gameInfoActions(res.value)
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
                .body(GamePlaySirenOutputModel(
                    properties = GameProperties(GameStateOutputModel.get(res.value)),
                    links = listOf(
                        LinkOutputModel(
                            relation = LinkRelation.SELF,
                            targetUri = Uris.placeShip(id)
                        ),
                        gameInfoLink(id)
                    )
                ))
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
                .body(res.value.toBoardOutputModel())
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
                .body(res.value.toBoardOutputModel())
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

    @GetMapping(Uris.GAME_BY_ID)
    fun getGameInfo(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getGame(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(GameSirenOutputModel(
                    properties = listOf(
                        "Game" to GameOutputModel(
                            gameId = res.value.gameId,
                            configuration = res.value.configuration,
                            player1 = res.value.player1,
                            player2 = res.value.player2,
                            state = GameStateOutputModel.get(res.value.state),
                            board1 = res.value.board1.toBoardOutputModel(),
                            board2 = res.value.board2.toBoardOutputModel(),
                        )
                    ),
                    actions = preparationSirenActions
                ))
            is Either.Left -> when (res.value) {
                GameError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }
}
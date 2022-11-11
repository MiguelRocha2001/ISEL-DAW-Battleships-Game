package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.hypermedia.*
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildBattleActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildPreparationActions
import pt.isel.daw.dawbattleshipgame.http.infra.siren
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
        val res = gameServices.startGame(
            user.id,
            Configuration(
                createGameInputModel.boardSize,
                createGameInputModel.fleet.map { it.key.toShipType() to it.value }.toSet(),
                createGameInputModel.nShotsPerRound,
                createGameInputModel.roundTimeout
            )
        )
        return when (res) {
            is Either.Right ->
                ResponseEntity.status(if (res.value.second != null) 201 else 202) // It depends if the game was created
                    .body(
                        siren(GameActionOutputModel(GameStateOutputModel.get(res.value.first), res.value.second)) {
                            links(startGameLinks(res.value.second))
                        }
                    )
            is Either.Left -> when (res.value) {
                GameCreationError.GameNotFound -> Problem.response(
                    404,
                    Problem.gameNotFound
                ) // TODO: check if this is the correct error
                GameCreationError.UserAlreadyInQueue -> Problem.response(405, Problem.userAlreadyInQueue)
                GameCreationError.UserAlreadyInGame -> Problem.response(405, Problem.userAlreadyInGame)
                GameCreationError.UnableToCreateGame -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @GetMapping(Uris.GAMES_GAME_ID)
    fun getCurrentGameId(user: User): ResponseEntity<*> {
        return when (val res = gameServices.getGameIdByUser(user.id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameIdOutputModel(res.value)) {
                    links(gameByIdLinks(user.id))
                })
            is Either.Left -> when (res.value) {
                GameIdError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                GameIdError.UserInGameQueue -> Problem.response(404, Problem.userInGameQueue)
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
            id,
            user.id,
            placeShipInputModel.shipType.toShipType(),
            Coordinate(placeShipInputModel.position.row, placeShipInputModel.position.column),
            placeShipInputModel.orientation.toOrientation()
        )
        return when (res) {
            is Either.Right -> ResponseEntity.status(201)
                .body(siren(GameActionOutputModel(GameStateOutputModel.get(res.value), id)) {
                    links(placeShipLinks(id))
                })
            is Either.Left -> when (res.value) {
                PlaceShipError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                PlaceShipError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                PlaceShipError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @PostMapping(Uris.GAMES_MOVE_SHIP)
    fun moveShip(
        user: User,
        @PathVariable id: Int,
        @RequestBody moveShipInputModel: MoveShipInputModel
    ): ResponseEntity<*> {
        return when (val res = gameServices.moveShip(id, user.id, moveShipInputModel.origin, moveShipInputModel.destination)) {
            is Either.Right -> ResponseEntity.status(201)
                .body(siren(GameActionOutputModel(GameStateOutputModel.get(res.value), id)) {
                    links(moveShipLinks(id))
                })
            is Either.Left -> when (res.value) {
                MoveShipError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                MoveShipError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                MoveShipError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @PostMapping(Uris.GAMES_ROTATE_SHIP)
    fun rotateShip(
        user: User,
        @PathVariable id: Int,
        @RequestBody coordinate: Coordinate
    ): ResponseEntity<*> {
        return when (val res = gameServices.rotateShip(id, user.id, coordinate)) {
            is Either.Right -> ResponseEntity.status(201)
                .body(siren(GameActionOutputModel(GameStateOutputModel.get(res.value), id)) {
                    links(rotateShipLinks(id))
                })
            is Either.Left -> when (res.value) {
                RotateShipError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                RotateShipError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                RotateShipError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @PostMapping(Uris.GAMES_CONFIRM_FLEET)
    fun confirmFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.confirmFleet(id, user.id)) {
            is Either.Right -> ResponseEntity.status(201)
                .body(siren(GameActionOutputModel(GameStateOutputModel.get(res.value), id)) {
                    links(confirmFleetLinks(id))
                })
            is Either.Left -> when (res.value) {
                FleetConfirmationError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                FleetConfirmationError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
            }
        }
    }

    @PostMapping(Uris.GAMES_PLACE_SHOT)
    fun placeShot(
        user: User,
        @PathVariable id: Int,
        @RequestBody coordinate: Coordinate
    ): ResponseEntity<*> {
        return when (val res = gameServices.placeShot(id, user.id, coordinate)) {
            is Either.Right -> ResponseEntity.status(201)
                .body(siren(GameActionOutputModel(GameStateOutputModel.get(res.value), id)) {
                    links(placeShotLinks(id))
                })
            is Either.Left -> when (res.value) {
                PlaceShotError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                PlaceShotError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                PlaceShotError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_MY_FLEET)
    fun getMyFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.getMyFleetLayout(id, user.id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()) {

                })
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_OPPONENT_FLEET)
    fun getOpponentFleet(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.getOpponentFleet(id, user.id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()) {

                })
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @GetMapping(Uris.GAMES_STATE)
    fun getGameState(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.getGameState(id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameStateOutputModel.get(res.value)) {

                })
            is Either.Left -> when (res.value) {
                GameStateError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @GetMapping(Uris.GAME_BY_ID)
    fun getGameInfo(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.getGame(id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(
                    GameOutputModel(
                        gameId = res.value.gameId,
                        configuration = res.value.configuration,
                        player1 = res.value.player1,
                        player2 = res.value.player2,
                        state = GameStateOutputModel.get(res.value.state),
                        board1 = res.value.board1.toBoardOutputModel(),
                        board2 = res.value.board2.toBoardOutputModel(),
                    )
                ) {
                    buildPreparationActions(this)
                    buildBattleActions(this)
                })
            is Either.Left -> when (res.value) {
                GameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @DeleteMapping(Uris.GAMES_DELETE)
    fun deleteGame(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        return when (val res = gameServices.deleteGame(id)) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value) {

                })
            is Either.Left -> when (res.value) {
                DeleteGameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }
}
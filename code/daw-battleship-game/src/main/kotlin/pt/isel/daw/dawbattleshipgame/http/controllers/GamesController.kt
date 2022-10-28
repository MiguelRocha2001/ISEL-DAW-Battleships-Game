package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration
import pt.isel.daw.dawbattleshipgame.http.hypermedia.*
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildBattleActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildPreparationActions
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.game.*
import pt.isel.daw.dawbattleshipgame.services.game.*
import pt.isel.daw.dawbattleshipgame.http.infra.siren

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
                createGameInputModel.configuration.boardSize,
                createGameInputModel.configuration.fleet.map { it.first.toShipType() to it.second }.toSet(),
                createGameInputModel.configuration.nShotsPerRound,
                createGameInputModel.configuration.roundTimeout
            )
        )
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(
                    siren(GameProperties(GameStateOutputModel.get(res.value))) {
                        links(startGameLinks(user.id))
                    }
                )
            is Either.Left -> when (res.value) {
                GameCreationError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                GameCreationError.UserAlreadyInQueue -> Problem.response(405, Problem.userAlreadyInQueue)
                GameCreationError.UserAlreadyInGame -> Problem.response(405, Problem.userAlreadyInGame)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET_GAME_ID)
    fun getCurrentGameId(user: User): ResponseEntity<*> {
        val res = gameServices.getGameIdByUser(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameIdOutputModel(res.value)) {
                    links(gameByIdLinks(user.id))
                })
            is Either.Left -> when (res.value) {
                GameIdError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
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
                .body(siren(GameProperties(GameStateOutputModel.get(res.value))) {
                    links(placeShipLinks(user.id))
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
        val res = gameServices.moveShip(user.id, moveShipInputModel.origin, moveShipInputModel.destination)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameProperties(GameStateOutputModel.get(res.value))) {
                    links(moveShipLinks(user.id))
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
        val res = gameServices.rotateShip(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameProperties(GameStateOutputModel.get(res.value))) {
                    links(rotateShipLinks(user.id))
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
        val res = gameServices.confirmFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameProperties(GameStateOutputModel.get(res.value))) {
                    links(confirmFleet(user.id))
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
        val res = gameServices.placeShot(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameProperties(GameStateOutputModel.get(res.value))) {
                    links(placeShotLinks(user.id))
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
        val res = gameServices.getMyFleetLayout(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()){

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
        val res = gameServices.getOpponentFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()){

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
        val res = gameServices.getGameState(user.id)
        return when (res) {
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
        @PathVariable gameId: Int
    ): ResponseEntity<*> {
        val res = gameServices.getGame(gameId)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameOutputModel(
                    gameId = res.value.gameId,
                    configuration = res.value.configuration,
                    player1 = res.value.player1,
                    player2 = res.value.player2,
                    state = GameStateOutputModel.get(res.value.state),
                    board1 = res.value.board1.toBoardOutputModel(),
                    board2 = res.value.board2.toBoardOutputModel(),
                )) {
                    buildPreparationActions(this)
                    buildBattleActions(this)
                })
            is Either.Left -> when (res.value) {
                GameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    /*
    @GetMapping(Uris.BATTLESHIPS_STATISTICS)
    fun getBattleshipsStatistics() {
        val res = gameServices.getBattleshipsStatistics()
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(BattleshipsStatisticsOutputModel()) {

                })
            is Either.Left -> when (res.value) {

            }
        }
    }

     */
}
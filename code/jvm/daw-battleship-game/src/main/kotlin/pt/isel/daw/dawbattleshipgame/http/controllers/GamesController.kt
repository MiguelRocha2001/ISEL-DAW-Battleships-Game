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
    @PostMapping(Uris.Games.My.ALL)
    fun createGame(
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
            is Either.Right -> {
                val gameId = res.value.second
                return if (gameId != null) {
                    ResponseEntity.status (201) // Games created
                    .header(
                        "Location",
                        Uris.Games.byId(gameId).toASCIIString()
                    ).body(
                        siren(GameInfoOutputModel(GameStateOutputModel.get(res.value.first), res.value.second)) {
                            Uris.Games.byId(gameId) to Rels.GAME_ID
                        }
                    )
                } else {
                    ResponseEntity.status (202) // Game not created but request was processed
                    .body(
                        siren(GameInfoOutputModel(GameStateOutputModel.get(res.value.first), res.value.second)) {
                            Uris.Games.My.CURRENT to Rels.GAME
                        }
                    )
                }
            }
            is Either.Left -> when (res.value) {
                GameCreationError.GameNotFound -> Problem.response(
                    404,
                    Problem.gameNotFound
                ) // TODO: check if this is the correct error
                GameCreationError.UserAlreadyInQueue -> Problem.response(405, Problem.userAlreadyInQueue)
                GameCreationError.UserAlreadyInGame -> Problem.response(405, Problem.userAlreadyInGame)
            }
        }
    }

    @GetMapping(Uris.Games.My.CURRENT_ID)
    fun getCurrentGameId(user: User): ResponseEntity<*> {
        val res = gameServices.getGameIdByUser(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameIdOutputModel(res.value)) {
                    gameByIdLinks(user.id)
                })
            is Either.Left -> when (res.value) {
                GameIdError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                GameIdError.UserInGameQueue -> Problem.response(404, Problem.userInGameQueue)
            }
        }
    }

    @PostMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun postShips(
        user: User,
        @RequestBody postShipInputModel: PostShipInputModel
    ): ResponseEntity<*> {
        return when (postShipInputModel) {
            is PlaceShipsInputModel -> {
                placeShips(user, postShipInputModel)
            }
            is AlterShipInputModel -> {
                updateShip(user, postShipInputModel)
            }
        }
    }

    fun placeShips(
        user: User,
        placeShipsInputModel: PlaceShipsInputModel
    ): ResponseEntity<*> {
        val res = gameServices.placeShips(
            user.id,
            placeShipsInputModel.ships.map { Triple(
                it.shipType.toShipType(),
                it.position.toCoordinate(),
                it.orientation.toOrientation()
            ) }
        )
        return when (res) {
            is Either.Right -> ResponseEntity.status(201).build<Unit>()
            is Either.Left -> when (res.value) {
                PlaceShipsError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                PlaceShipsError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                PlaceShipsError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    fun updateShip(
        user: User,
        alterShipInputModel: AlterShipInputModel
    ): ResponseEntity<*> {
        val res = gameServices.updateShip(
            user.id,
            alterShipInputModel.origin.toCoordinate(),
            alterShipInputModel.destination?.toCoordinate()
        )
        return when (res) {
            is Either.Right -> ResponseEntity.status(204).build<Unit>()
            is Either.Left -> when (res.value) {
                UpdateShipError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                UpdateShipError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                UpdateShipError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @PutMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun updateFleet(
        user: User,
        @RequestBody fleetStateInputModel: FleetStateInputModel
    ): ResponseEntity<*> {
        val res = gameServices.updateFleetState(user.id, fleetStateInputModel.fleetConfirmed)
        return when (res) {
            is Either.Right -> ResponseEntity.status(204).build<Unit>()
            is Either.Left -> when (res.value) {
                FleetConfirmationError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                FleetConfirmationError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
            }
        }
    }

    @PostMapping(Uris.Games.My.Current.My.Shots.ALL)
    fun placeShot(
        user: User,
        @RequestBody coordinate: Coordinate
    ): ResponseEntity<*> {
        val res = gameServices.placeShot(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(204).build<Unit>() // TODO -> add header
            is Either.Left -> when (res.value) {
                PlaceShotError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
                PlaceShotError.ActionNotPermitted -> Problem.response(405, Problem.actionNotPermitted)
                PlaceShotError.InvalidMove -> Problem.response(405, Problem.invalidMove)
            }
        }
    }

    @GetMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun getMyFleet(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getMyFleetLayout(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()) {

                })
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    @GetMapping(Uris.Games.My.Current.Opponent.Ships.ALL)
    fun getOpponentFleet(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getOpponentFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value.toBoardOutputModel()) {

                })
            is Either.Left -> when (res.value) {
                GameSearchError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }

    /*
    @GetMapping(Uris.Games.BY_ID)
    fun getGameState(
        user: User,
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.getGameState(id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(GameStateOutputModel.get(res.value)) {

                })
            is Either.Left -> when (res.value) {
                GameStateError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }
     */

    @GetMapping(Uris.Games.My.CURRENT)
    fun getGame(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getGameByUser(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(
                    GameOutputModel(
                        gameId = res.value.id,
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

    @DeleteMapping(Uris.Games.BY_ID)
    fun deleteGame(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.deleteGame(id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(siren(res.value) {

                })
            is Either.Left -> when (res.value) {
                DeleteGameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            }
        }
    }
}
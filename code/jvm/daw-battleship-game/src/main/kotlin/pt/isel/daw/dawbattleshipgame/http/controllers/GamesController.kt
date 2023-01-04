package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.JsonMediaType
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.hypermedia.*
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.battleActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.preparationActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.quitGameAction
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.game.*
import pt.isel.daw.dawbattleshipgame.http.model.map
import pt.isel.daw.dawbattleshipgame.services.game.*

@RestController
class GamesController(
    private val gameServices: GameServices
) {

    @GetMapping(Uris.Games.My.CURRENT)
    fun getGame(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getCurrentGameByUser(user.id)
        return res.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(
                    GameOutputModel(
                        gameId = it.first.id,
                        configuration = it.first.configuration,
                        player1 = it.first.player1,
                        player2 = it.first.player2,
                        state = GameStateOutputModel.get(it.first.state),
                        board1 = it.first.board1.toBoardOutputModel(),
                        board2 = it.first.board2.toBoardOutputModel(),
                        playerTurn = it.first.playerTurn,
                        winner = it.first.winner,
                        myPlayer = PlayerOutputModel.get(it.second)
                    )
                ) {
                    preparationActions(this)
                    battleActions(this)
                    quitGameAction(this)
                    clazz("game")
                })
        }
    }

    /**
     * Create a game, if there is no request body makes a quick game
     */
    @PostMapping(Uris.Games.My.ALL)
    fun createGame(
        user: User,
        @RequestBody(required = false)
        createGameInputModel: CreateGameInputModel?
    ): ResponseEntity<*> {
        val res = gameServices.startGame(
                user.id,
                createGameInputModel?.toConfiguration()
        )
        return res.map {
            val gameId = it.second
            if (gameId != null) {
                ResponseEntity.status (201) // Games created
                    .contentType(SirenMediaType)
                    .header(
                        "Location",
                        Uris.Games.byId(gameId).toASCIIString()
                    ).body(
                        siren(GameInfoOutputModel(GameStateOutputModel.get(it.first), it.second)) {
                            Uris.Games.My.CURRENT to Rels.GAME
                        }
                    )
            } else {
                ResponseEntity.status(202) // Game not created but request was processed
                    .contentType(SirenMediaType)
                    .body(
                        siren(GameInfoOutputModel(GameStateOutputModel.get(it.first), null)) {
                            Uris.Games.My.CURRENT to Rels.GAME
                        }
                    )
            }
        }
    }

    @GetMapping(Uris.Games.My.CURRENT_ID)
    fun getCurrentGameId(user: User): ResponseEntity<*> {
        val res = gameServices.getGameIdByUser(user.id)
        return res.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(GameIdOutputModel(it)) {
                    gameByIdLinks(user.id)
                    clazz("game")
                })
        }
    }

    @PostMapping(Uris.Games.My.CURRENT_ID)
    fun quitCurrentGame(user: User): ResponseEntity<*> {
        val res = gameServices.getGameIdByUser(user.id)
        return res.map {
            ResponseEntity.status(200)
                    .contentType(SirenMediaType)
                    TODO()
        }
    }

    @PostMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun postShips(
        user: User,
        @RequestBody postShipsInputModel: PostShipsInputModel
    ): ResponseEntity<*> {
        return when (postShipsInputModel) {
            is PlaceShipsInputModel -> {
                placeShips(user, postShipsInputModel)
            }
            is AlterShipInputModel -> {
                updateShip(user, postShipsInputModel)
            }
        }
    }

    fun placeShips(
        user: User,
        placeShipsInputModel: PlaceShipsInputModel
    ): ResponseEntity<*> {
        println(placeShipsInputModel)
        val res = gameServices.placeShips(
            user.id,
            placeShipsInputModel.ships.map { Triple(
                it.shipType.toShipType(),
                it.position.toCoordinate(),
                it.orientation.toOrientation()
            )},
            placeShipsInputModel.fleetConfirmed
        )
        return res.map {
            ResponseEntity.status(201)
                .contentType(JsonMediaType)
                .build<Unit>()
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
        return res.map {
            ResponseEntity.status(204)
                .contentType(JsonMediaType)
                .build<Unit>()
        }
    }

    @PutMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun updateFleet(
        user: User,
        @RequestBody fleetStateInputModel: FleetStateInputModel
    ): ResponseEntity<*> {
        val res = gameServices.updateFleetState(user.id, fleetStateInputModel.fleetConfirmed)
        return res.map {
            ResponseEntity.status(204)
                .contentType(JsonMediaType)
                .build<Unit>()
        }
    }

    @PostMapping(Uris.Games.My.Current.My.Shots.ALL)
    fun placeShots(
        user: User,
        @RequestBody shots: ShotsInputModel
    ): ResponseEntity<*> {
        val res = gameServices.placeShots(user.id, shots.toListCoordinate())
        return res.map {
            ResponseEntity.status(204)
                .contentType(JsonMediaType)
                .build<Unit>() // TODO -> add header
        }
    }

    @GetMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun getMyFleet(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getMyFleetLayout(user.id)
        return res.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it.toBoardOutputModel()) {
                    clazz("ships")
                })
        }
    }

    @GetMapping(Uris.Games.My.Current.Opponent.Ships.ALL)
    fun getOpponentFleet(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getOpponentFleet(user.id)
        return res.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it.toBoardOutputModel()) {
                    clazz("ships")
                })
        }
    }

    @PostMapping(Uris.Games.BY_ID)
    fun quitGame(
        user: User,
        gameId: Int
    ): ResponseEntity<*> {
        val res = gameServices.quitGame(user.id, gameId)
        return res.map {
            ResponseEntity.status(204)
                .contentType(JsonMediaType)
                .build<Unit>()
        }
    }

    @DeleteMapping(Uris.Games.BY_ID)
    fun deleteGame(
        @PathVariable id: Int
    ): ResponseEntity<*> {
        val res = gameServices.deleteGame(id)
        return res.map {
            ResponseEntity.status(200)
                .contentType(SirenMediaType)
                .body(siren(it) {
                    clazz("game")

                })
        }
    }
}
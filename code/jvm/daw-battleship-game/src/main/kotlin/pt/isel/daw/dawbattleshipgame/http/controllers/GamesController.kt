package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.hypermedia.*
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildBattleActions
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildPreparationActions
import pt.isel.daw.dawbattleshipgame.http.infra.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.game.*
import pt.isel.daw.dawbattleshipgame.http.model.map
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
                            Uris.Games.byId(gameId) to Rels.GAME_ID
                        }
                    )
            } else {
                ResponseEntity.status(202) // Game not created but request was processed
                    .contentType(SirenMediaType)
                    .body(
                        siren(GameInfoOutputModel(GameStateOutputModel.get(it.first), it.second)) {
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
            ResponseEntity.status(201).build<Unit>()
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
            ResponseEntity.status(204).build<Unit>()
        }
    }

    @PutMapping(Uris.Games.My.Current.My.Ships.ALL)
    fun updateFleet(
        user: User,
        @RequestBody fleetStateInputModel: FleetStateInputModel
    ): ResponseEntity<*> {
        val res = gameServices.updateFleetState(user.id, fleetStateInputModel.fleetConfirmed)
        return res.map {
            ResponseEntity.status(204).build<Unit>()
        }
    }

    @PostMapping(Uris.Games.My.Current.My.Shots.ALL)
    fun placeShot(
        user: User,
        @RequestBody coordinate: CoordinateInputModel
    ): ResponseEntity<*> {
        val res = gameServices.placeShot(user.id, coordinate.toCoordinate())
        return res.map {
            ResponseEntity.status(204).build<Unit>() // TODO -> add header
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

    @GetMapping(Uris.Games.My.CURRENT)
    fun getGame(
        user: User,
    ): ResponseEntity<*> {
        val res = gameServices.getGameByUser(user.id)
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
                        myPlayer = PlayerOutputModel.get(it.second)
                    )
                ) {
                    buildPreparationActions(this)
                    buildBattleActions(this)
                    clazz("game")

                })
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
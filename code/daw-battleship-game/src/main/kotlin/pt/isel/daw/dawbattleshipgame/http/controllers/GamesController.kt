package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.services.user.TokenCreationError
import pt.isel.daw.dawbattleshipgame.http.model.user.UserTokenCreateOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.CreateGameInputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.MoveShipInputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.BoardOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.PlaceShipInputModel
import pt.isel.daw.dawbattleshipgame.services.game.*

@RestController
class GamesController(
    private val gameServices: GameServices
) {
    @PostMapping(Uris.GAMES_CREATE)
    fun create(user: User, @RequestBody createGameInputModel: CreateGameInputModel): ResponseEntity<*> {
        val res = gameServices.startGame(user.id, createGameInputModel.configuration)
        return when (res) {
            is Either.Right -> ResponseEntity.status(201)
                .header(
                    "Location",
                ).build<Unit>()
            is Either.Left -> when (res.value) {
                GameCreationError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
                GameCreationError.UserAlreadyInQueue -> Problem.response(405, Problem.toBeChanged)
                GameCreationError.UserAlreadyInGame -> Problem.response(405, Problem.toBeChanged)
            }
        }
    }

    @PostMapping(Uris.GAMES_PLACE_SHIP)
    fun placeShip(user: User, @RequestBody placeShipInputModel: PlaceShipInputModel): ResponseEntity<*> {
        val res = gameServices.placeShip(user.id, placeShipInputModel.shipType, placeShipInputModel.position, placeShipInputModel.orientation)
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
    fun moveShip(user: User, @RequestBody moveShipInputModel: MoveShipInputModel): ResponseEntity<*> {
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
    fun rotateShip(user: User, @RequestBody coordinate: Coordinate): ResponseEntity<*> {
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
    fun confirmFleet(user: User): ResponseEntity<*> {
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
    fun placeShot(user: User, @RequestBody coordinate: Coordinate): ResponseEntity<*> {
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
    fun getMyFleet(user: User): ResponseEntity<*> {
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
    fun getOpponentFleet(user: User): ResponseEntity<*> {
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
    fun getGameState(user: User): ResponseEntity<*> {
        val res = gameServices.getGameState(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                GameStateError.GameNotFound -> Problem.response(404, Problem.toBeChanged)
            }
        }
    }
}
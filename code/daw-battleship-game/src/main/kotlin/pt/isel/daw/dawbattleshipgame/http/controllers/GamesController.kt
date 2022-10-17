package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.services.UserServices
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.services.TokenCreationError
import pt.isel.daw.dawbattleshipgame.services.UserCreationError
import pt.isel.daw.dawbattleshipgame.http.model.UserCreateInputModel
import pt.isel.daw.dawbattleshipgame.http.model.UserCreateTokenInputModel
import pt.isel.daw.dawbattleshipgame.http.model.UserHomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.UserTokenCreateOutputModel
import pt.isel.daw.dawbattleshipgame.services.GameServices

@RestController
class GamesController(
    private val gameServices: GameServices
) {
    @PostMapping(Uris.GAMES_CREATE)
    fun create(user: User, @RequestBody createGameInputModel: CreateGameInputModel): ResponseEntity<*> {
        val res = gameServices.startGame(user.id, createGameInputModel.configuration)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.GAMES_PLACE_SHIP)
    fun placeShip(user: User, @RequestBody placeShipInputModel: PlaceShipInputModel): ResponseEntity<*> {
        val res = gameServices.placeShip(user.id, placeShipInputModel.shipType, placeShipInputModel.position, placeShipInputModel.orientation)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.GAMES_MOVE_SHIP)
    fun moveShip(user: User, @RequestBody moveShipInputModel: MoveShipInputModel): ResponseEntity<*> {
        val res = gameServices.moveShip(user.id, moveShipInputModel.origin, moveShipInputModel.destination)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.GAMES_ROTATE_SHIP)
    fun rotateShip(user: User, @RequestBody coordinate: Coordinate): ResponseEntity<*> {
        val res = gameServices.rotateShip(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.GAMES_ROTATE_SHIP)
    fun confirmFleet(user: User): ResponseEntity<*> {
        val res = gameServices.confirmFleet(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @PostMapping(Uris.GAMES_ROTATE_SHIP)
    fun placeShot(user: User, @RequestBody coordinate: Coordinate): ResponseEntity<*> {
        val res = gameServices.placeShot(user.id, coordinate)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @GetMapping(Uris.GAMES_GET)
    fun getMyFleet(user: User) {
        val res = gameServices.getMyFleetLayout(user.id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }
}
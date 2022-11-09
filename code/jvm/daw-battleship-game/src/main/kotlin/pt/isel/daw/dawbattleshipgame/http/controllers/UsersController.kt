package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildStartGameAction
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.createTokenSirenAction
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.game.UserStatOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.UserStatsOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.user.*
import pt.isel.daw.dawbattleshipgame.services.user.TokenCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserDeletionError
import pt.isel.daw.dawbattleshipgame.services.user.UserServices

@RestController
class UsersController(
    private val userService: UserServices
) {
    @PostMapping(Uris.USERS_CREATE)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.password)
        return when (res) {
            is Either.Right -> ResponseEntity.status(201)
                .header(
                    "Location",
                    Uris.userById(res.value).toASCIIString()
                )
                .body(siren(UserCreateOutputModel(res.value)) {
                    link(Uris.userCreate(), Rels.SELF)
                    createTokenSirenAction(this)
                })
            is Either.Left -> when (res.value) {
                UserCreationError.InsecurePassword -> Problem.response(400, Problem.insecurePassword)
                UserCreationError.UserAlreadyExists -> Problem.response(400, Problem.userAlreadyExists)
            }
        }
    }

    @PostMapping(Uris.USERS_TOKEN)
    fun token(@RequestBody input: UserCreateTokenInputModel): ResponseEntity<*> {
        val res = userService.createToken(input.username, input.password)
        return when (res) {
            is Either.Right -> ResponseEntity.status(201)
                .body(
                    siren(TokenOutputModel(res.value)) {
                        link(Uris.createToken(), Rels.SELF)
                        link(Uris.userHome(), Rels.USER_HOME)
                        buildStartGameAction(this)
                    })
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @GetMapping(Uris.USERS_BY_ID)
    fun getById(@PathVariable id: String) {
        TODO("TODO")
    }

    @GetMapping(Uris.USER_HOME)
    fun getUserHome(user: User): ResponseEntity<*> {
        return ResponseEntity.status(201)
            .body(
                siren(UserHomeOutputModel(user.id, user.username)) {
                    link(Uris.userHome(), Rels.SELF)
                    link(Uris.currentGameId(), Rels.GAME_ID)
                    buildStartGameAction(this)
                }
            )
    }

    @DeleteMapping(Uris.USERS_BY_ID)
    fun deleteUser(@PathVariable id: Int): ResponseEntity<*> {
        val res = userService.deleteUser(id)
        return when (res) {
            is Either.Right -> ResponseEntity.status(204)
                .body(siren(res.value) {

                })
            is Either.Left -> when (res.value) {
                UserDeletionError.UserDoesNotExist -> Problem.response(400, Problem.userNotFound)
            }
        }
    }

    @GetMapping(Uris.USERS_STATS)
    fun getUserStatistics(): ResponseEntity<*> {
        val res = userService.getUserRanking()
        val userStats = res.map { UserStatOutputModel(it.username, it.wins, it.gamesPlayed) }
        return ResponseEntity.status(200)
            .body(siren(UserStatsOutputModel(userStats)) {
                link(Uris.usersStats(), Rels.SELF)
            })
    }
}
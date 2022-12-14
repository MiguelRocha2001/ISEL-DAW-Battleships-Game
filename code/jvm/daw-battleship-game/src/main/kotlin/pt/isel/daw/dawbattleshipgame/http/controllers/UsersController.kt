package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.buildStartGameAction
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.createTokenSirenAction
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.game.UserStatOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.game.UserStatsOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.map
import pt.isel.daw.dawbattleshipgame.http.model.user.*
import pt.isel.daw.dawbattleshipgame.services.user.UserServices

@RestController
class UsersController(
    private val userService: UserServices
) {
    @PostMapping(Uris.Users.ALL)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.password)
        return res.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .header(
                    "Location",
                    Uris.Users.byId(it).toASCIIString()
                )
                .body(siren(UserCreateOutputModel(it)) {
                    link(Uris.Users.create(), Rels.SELF)
                    createTokenSirenAction(this)
                    clazz("user")
                })
        }
    }

    @PostMapping(Uris.Users.TOKEN)
    fun token(@RequestBody input: UserCreateTokenInputModel): ResponseEntity<*> {
        val res = userService.createToken(input.username, input.password)
        return res.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .body(
                    siren(TokenOutputModel(it)) {
                        link(Uris.Users.createToken(), Rels.SELF)
                        link(Uris.Users.home(), Rels.USER_HOME)
                        buildStartGameAction(this)
                        clazz("user-token")

                    }
                )
        }
    }

    @GetMapping(Uris.Users.BY_ID)
    fun getById(@PathVariable id: Int) : ResponseEntity<*>{
        val user = userService.getUserById(id) ?:
            return Problem.response(404, Problem.userNotFound)
        return TODO()
    }

    @GetMapping(Uris.Users.HOME)
    fun getUserHome(user: User): ResponseEntity<*> {
        return ResponseEntity.status(201)
            .contentType(SirenMediaType)
            .body(
                siren(UserHomeOutputModel(user.id, user.username)) {
                    link(Uris.Users.home(), Rels.SELF)
                    link(Uris.Games.My.current(), Rels.GAME_ID)
                    link(Uris.Games.My.current(), Rels.GAME)
                    buildStartGameAction(this)
                    clazz("user-home")
                }
            )
        //TODO( see errors related to this -- Unauthorized 401 )
    }

    @DeleteMapping(Uris.Users.BY_ID)
    fun deleteUser(@PathVariable id: Int): ResponseEntity<*> {
        val res = userService.deleteUser(id)
        return res.map {
            ResponseEntity.status(204)
                .contentType(SirenMediaType)
                .body(siren(it) {
                    clazz("user")
                })
        }
    }

    @GetMapping(Uris.Users.STATS)
    fun getUserStatistics(): ResponseEntity<*> {
        val res = userService.getUserRanking()
        val userStats = res.map { UserStatOutputModel(it.username, it.wins, it.gamesPlayed) }
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(UserStatsOutputModel(userStats)) {
                    link(Uris.Users.stats(), Rels.SELF)
                    clazz("user-stats")
            })
    }
}
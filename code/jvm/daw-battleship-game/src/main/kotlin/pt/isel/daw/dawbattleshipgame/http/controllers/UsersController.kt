package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.SirenMediaType
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.createGameSirenAction
import pt.isel.daw.dawbattleshipgame.http.hypermedia.actions.createTokenSirenAction
import pt.isel.daw.dawbattleshipgame.http.infra.siren
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.http.model.map
import pt.isel.daw.dawbattleshipgame.http.model.user.*
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
class UsersController(
    private val userService: UserServices
) {
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Problem> {
        return Problem.buildInputError(ex.value.toString())
    }

    @PostMapping(Uris.Users.ALL)
    fun create(
        @RequestBody input: UserCreateInputModel
    ): ResponseEntity<*> {
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

    @GetMapping(Uris.Users.TOKEN)
    fun isLogged(user: User): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

    @DeleteMapping(Uris.Users.TOKEN)
    fun logout(
        user: User,
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        val cookie = buildCookie(0, null)
        response.addCookie(cookie)

        return ResponseEntity.status(204).build()
    }

    @PostMapping(Uris.Users.TOKEN)
    fun token(
        @RequestBody input: UserCreateTokenInputModel,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val res = userService.createToken(input.username, input.password)

        // adds cookie to response
        if (res is Either.Right) {
            val cookie = buildCookie(60 * 60 * 24 * 7, res.value)
            response.addCookie(cookie)
        }

        return res.map {
            ResponseEntity.status(201)
                .contentType(SirenMediaType)
                .body(
                    siren(TokenOutputModel(it)) {
                        link(Uris.Users.createToken(), Rels.SELF)
                        link(Uris.Users.home(), Rels.USER_HOME)
                        createGameSirenAction(this)
                        clazz("user-token")
                    }
                )
        }
    }

    private fun buildCookie(maxAge: Int, value: String?): Cookie {
        val cookieWithToken = Cookie("token", value ?: "null")
        cookieWithToken.path = "/"
        cookieWithToken.isHttpOnly = true
        cookieWithToken.secure = true
        cookieWithToken.maxAge = maxAge

        return cookieWithToken
    }

    @GetMapping(Uris.Users.ALL)
    fun getAll(): ResponseEntity<*> {
        val res = userService.getAllUsers()
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(
                siren(UserListOutputModel(res.map { it.toUserOutputModel() })) {
                    link(Uris.Users.all(), Rels.SELF)
                    clazz("user-list")
                }
            )
    }

    @GetMapping(Uris.Users.BY_ID1)
    fun getById(
        @PathVariable id: Int
    ) : ResponseEntity<*> {
        val user = userService.getUserById(id)?.toUserStatOutputModel() ?:
            return Problem.response(404, Problem.userNotFound)
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(
                siren(user) {
                    link(Uris.Users.byId(id), Rels.SELF)
                    clazz("user")
                }
            )
    }

    @GetMapping(Uris.Users.HOME)
    fun getUserHome(user: User): ResponseEntity<*> {
        return ResponseEntity.status(201)
            .contentType(SirenMediaType)
            .body(
                siren(UserStatOutputModel(user.id, user.username, user.wins, user.gamesPlayed)) {
                    link(Uris.Users.home(), Rels.SELF)
                    link(Uris.Games.My.current(), Rels.GAME_ID)
                    link(Uris.Games.My.current(), Rels.GAME)
                    createGameSirenAction(this)
                    clazz("user-home")
                }
            )
    }

    @DeleteMapping(Uris.Users.BY_ID1)
    fun deleteUser(
        @PathVariable id: Int
    ): ResponseEntity<*> {
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
    fun getUserStats(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) pageSize: Int?
    ): ResponseEntity<*> {
        val res = userService.getUserRanking(page, pageSize)
        val userStats = res.map { UserStatOutputModel(it.id, it.username, it.wins, it.gamesPlayed) }
        return ResponseEntity.status(200)
            .contentType(SirenMediaType)
            .body(siren(UserStatsOutputModel(userStats)) {
                    link(Uris.Users.stats(), Rels.SELF)
                    clazz("user-stats")
            })
    }
}
package pt.isel.daw.dawbattleshipgame.http.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.dawbattleshipgame.Either
import pt.isel.daw.dawbattleshipgame.domain.player.User
import pt.isel.daw.dawbattleshipgame.http.hypermedia.SirenAction
import pt.isel.daw.dawbattleshipgame.services.user.UserServices
import pt.isel.daw.dawbattleshipgame.http.model.Problem
import pt.isel.daw.dawbattleshipgame.services.user.TokenCreationError
import pt.isel.daw.dawbattleshipgame.services.user.UserCreationError
import pt.isel.daw.dawbattleshipgame.http.model.UserCreateInputModel
import pt.isel.daw.dawbattleshipgame.http.model.UserCreateTokenInputModel
import pt.isel.daw.dawbattleshipgame.http.model.user.UserHomeOutputModel
import pt.isel.daw.dawbattleshipgame.http.model.user.UserTokenOutputModel
import java.net.URI

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
                ).build<Unit>()
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
                    UserTokenOutputModel(
                        properties = listOf(
                            "token" to res.value
                        ),
                        actions = listOf(
                            SirenAction(
                                name = "create-user",
                                title = "Create User",
                                method = HttpMethod.POST,
                                href = URI(Uris.USERS_CREATE),
                                type = MediaType.APPLICATION_JSON,
                                fields = listOf(
                                    SirenAction.Field(
                                        name = "username",
                                        type = "text",
                                        title = "Username"
                                    ),
                                    SirenAction.Field(
                                        name = "password",
                                        type = "hidden",
                                        title = "Password"
                                    )
                                )
                            )
                        )
                    )
                )
            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.response(400, Problem.userOrPasswordAreInvalid)
            }
        }
    }

    @GetMapping(Uris.USERS_GET_BY_ID)
    fun getById(@PathVariable id: String) {
        TODO("TODO")
    }

    @GetMapping(Uris.USER_HOME)
    fun getUserHome(user: User): UserHomeOutputModel {
        return UserHomeOutputModel(
            id = user.id.toString(),
            username = user.username,
        )
    }
}
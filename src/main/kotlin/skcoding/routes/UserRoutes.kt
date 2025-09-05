package Server.skcoding.routes

import Server.skcoding.data.models.User
import Server.skcoding.data.requests.CreateAccountRequest
import Server.skcoding.data.requests.LoginRequest
import Server.skcoding.data.responses.AuthResponse
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages.FIELDS_BLANK
import Server.skcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import Server.skcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import Server.skcoding.util.QueryParams
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.createUser(userService: UserService) {
    // val authService: AuthService by application.inject()
        post("/api/user/create") {
            val request = call.receiveNullable<CreateAccountRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if(userService.doesUserWithEmailExist(request.email)) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS,
                    )
                )
                return@post
            }
            when(userService.validateCreateAccountRequest(request)) {
                is UserService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        BasicApiResponse(
                            successful = false,
                            message = FIELDS_BLANK,
                        )
                    )
                    return@post
                }
                is UserService.ValidationEvent.Success -> {
                    userService.createUser(request)
                    call.respond(
                        BasicApiResponse(successful = true)
                    )
                }
            }
        }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
) {

    post ("/api/user/login"){
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if(request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = INVALID_CREDENTIALS,
                )
            )
            return@post
        }
        val isCorrectPassword = userService.isValidPassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )
        if(isCorrectPassword) {
            val expiresIn = 1000 * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId", user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(token = token)
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = false,
                    message = INVALID_CREDENTIALS,
                )
            )
        }
    }
}

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if(query == null || query.isBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<User>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResults
            )
        }
    }
}











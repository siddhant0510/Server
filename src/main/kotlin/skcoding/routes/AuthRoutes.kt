package Server.skcoding.routes

import Server.skcoding.data.requests.CreateAccountRequest
import Server.skcoding.data.requests.LoginRequest
import Server.skcoding.data.responses.AuthResponse
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages.FIELDS_BLANK
import Server.skcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import Server.skcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.Date

fun Route.createUser(userService: UserService) {
    // val authService: AuthService by application.inject()
    post("/api/user/create") {
        val request = call.receiveNullable<CreateAccountRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if(userService.doesUserWithEmailExist(request.email)) {
            call.respond(
                BasicApiResponse<Unit>(
                    successful = false,
                    message = USER_ALREADY_EXISTS,
                )
            )
            return@post
        }
        when(userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = FIELDS_BLANK,
                    )
                )
                return@post
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    BasicApiResponse<Unit>(successful = true)
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
                BasicApiResponse<Unit>(
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
                BasicApiResponse(
                    successful = true,
                    data = AuthResponse(token = token)
                )
            )
        } else {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse<Unit>(
                    successful = false,
                    message = INVALID_CREDENTIALS,
                )
            )
        }
    }
}

fun Route.authenticate() {
    authenticate {
        get("/api/user/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}
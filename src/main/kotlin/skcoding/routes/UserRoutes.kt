package Server.skcoding.routes

import Server.skcoding.data.repository.user.UserRepository
import Server.skcoding.data.models.User
import Server.skcoding.data.requests.CreateAccountRequest
import Server.skcoding.data.requests.LoginRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.util.ApiResponseMessages.FIELDS_BLANK
import Server.skcoding.util.ApiResponseMessages.INVALID_CREDENTIALS
import Server.skcoding.util.ApiResponseMessages.USER_ALREADY_EXISTS
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.userRoutes() {
    val userRepository: UserRepository by application.inject()
    // val authService: AuthService by application.inject()
        post("/api/user/create") {
            val request = call.receiveNullable<CreateAccountRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = userRepository.getUserByEmail(request.email) != null
            if(userExist) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS,
                    )
                )
                return@post
            }
            if(request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELDS_BLANK,
                    )
                )
                return@post
            }
            userRepository.createUser(
                User(
                    email = request.email,
                    username = request.username,
                    password = request.password,
                    profileImageUrl = "",
                    bio = "",
                    gitHubUrl = null,
                    instagramUrl = null,
                    linkedInUrl = null
                )
            )
            call.respond(
                BasicApiResponse(successful = true)
            )
        }
}

fun Route.loginUser() {
    val userRepository: UserRepository by application.inject()
    post ("/api/user/login"){
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if(request.email.isBlank() || request.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val isCorrectPassword = userRepository.doesPasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
        if(isCorrectPassword) {
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true
                )
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













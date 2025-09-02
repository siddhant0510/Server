package Server.skcoding.routes

import Server.skcoding.data.models.Post
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.createPostRoute(
    postService: PostService,
    userService: UserService
) {

    authenticate {
        post("/api/post/create"){
            val request = call.receiveNullable<CreatePostRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val email = call.principal<JWTPrincipal>()?.getClaim("email", String::class)
            val isEmailByUser = userService.doesEmailBelongToUserId(
                email = email ?: "",
                userId = request.userId
            )
            if(!isEmailByUser) {
                call.respond(HttpStatusCode.Unauthorized, "You are not who you say you are.")
                return@post
            }

            val didUserExist = postService.createPostIfUserExists(request)
            if(!didUserExist) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        successful = true
                    )
                )
            }
        }
    }
}
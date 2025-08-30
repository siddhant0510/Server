package Server.skcoding.routes

import Server.skcoding.data.models.Post
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.PostService
import Server.skcoding.util.ApiResponseMessages
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.createPostRoute(postService: PostService) {

    post("/api/post/create"){
        val request = call.receiveNullable<CreatePostRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
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
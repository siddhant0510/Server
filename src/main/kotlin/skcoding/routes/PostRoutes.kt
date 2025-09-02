package Server.skcoding.routes

import Server.skcoding.data.models.Post
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.plugins.email
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import Server.skcoding.util.Constants
import Server.skcoding.util.QueryParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
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
            ifEmailBelongsToUser(
                call = call,
                userId = request.userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
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
}

fun Route.getPostsForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            ifEmailBelongsToUser(
                call = call,
                userId = userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
                val posts = postService.getPostsForFollows(userId, page, pageSize)
                call.respond(HttpStatusCode.OK, posts)
            }
        }
    }
}
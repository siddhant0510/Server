package Server.skcoding.routes

import Server.skcoding.data.models.Like
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.data.requests.DeletePostRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.CommentService
import Server.skcoding.service.LikeService
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import Server.skcoding.util.Constants
import Server.skcoding.util.QueryParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.createPost(
    postService: PostService,
) {

    authenticate {
        post("/api/post/create"){
            val request = call.receiveNullable<CreatePostRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userId = call.userId
            val didUserExist = postService.createPostIfUserExists(request, userId)
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

fun Route.getPostsForFollows(
    postService: PostService,
) {
    authenticate {
        get("/api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(HttpStatusCode.OK, posts)
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
) {
    authenticate {
        delete("/api/post/delete"){
            val request = call.receiveNullable<DeletePostRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val post = postService.getPost(request.postId)
            if(post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            if(post.userId == call.userId) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsForPost(request.postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }

        }
    }

}
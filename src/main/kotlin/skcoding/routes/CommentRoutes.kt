package Server.skcoding.routes

import Server.skcoding.data.requests.CreateCommentRequest
import Server.skcoding.data.requests.DeleteCommentRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.CommentService
import Server.skcoding.service.LikeService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import Server.skcoding.util.QueryParams
import com.sun.net.httpserver.HttpsServer
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createComment(
    commentService: CommentService,
    userService: UserService
) {
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveNullable<CreateCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            ifEmailBelongsToUser(
                call = call,
                userId = request.userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
                when(commentService.createComment(request)) {
                    is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.FIELDS_BLANK
                            )
                        )
                    }
                    is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                        call.respond(
                            HttpStatusCode.OK,
                            BasicApiResponse(
                                successful = false,
                                message = ApiResponseMessages.COMMENT_TOO_LONG
                            )
                        )
                    }
                    is CommentService.ValidationEvent.Success -> {
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
}

fun Route.getCommentsForPost(
    commentService: CommentService,
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val comments = commentService.getCommentsForPost(postId)
            call.respond(HttpStatusCode.OK, comments)
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    userService: UserService,
    likeService: LikeService
) {
    authenticate {
        delete ("/api/comment/delete"){
            val request = call.receiveNullable<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            ifEmailBelongsToUser(
                call = call,
                userId = request.userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
                val deleted = commentService.deleteComment(request.commentId)
                if(deleted) {
                    likeService.deleteLikesForParent(request.commentId)
                    call.respond(HttpStatusCode.OK, BasicApiResponse(successful = true))
                } else {
                    call.respond(HttpStatusCode.NotFound, BasicApiResponse(successful = false))
                }
            }
        }
    }
}
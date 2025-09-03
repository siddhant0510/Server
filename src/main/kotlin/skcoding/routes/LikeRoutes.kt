package Server.skcoding.routes

import Server.skcoding.data.requests.LikeUpdateRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.LikeService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

fun Route.likeParent(
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        post("/api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            ifEmailBelongsToUser(
                call = call,
                userId = request.userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
                val likeSuccessful = likeService.likeParent(request.userId, request.parentId)
                if(likeSuccessful) {
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
                            message = ApiResponseMessages.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}

fun Route.unlikeParent(
    likeService: LikeService,
    userService: UserService
) {
    authenticate {
        delete("/api/unlike") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            ifEmailBelongsToUser(
                call = call,
                userId = request.userId,
                validateEmail = { email, userId -> userService.doesEmailBelongToUserId(email, userId) }
            ) {
                val unlikeSuccessful = likeService.unlikeParent(request.userId, request.parentId)
                if(unlikeSuccessful) {
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
                            message = ApiResponseMessages.USER_NOT_FOUND
                        )
                    )
                }
            }
        }
    }
}
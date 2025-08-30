package Server.skcoding.routes

import Server.skcoding.data.repository.follow.FollowRepository
import Server.skcoding.data.requests.FollowUpdateRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.FollowService
import Server.skcoding.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Route.followUser(followService: FollowService) {

    post("/api/following/follow") {
        val request = call.receiveNullable<FollowUpdateRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val didUserExist = followService.followUserIfExists(request)
        if(didUserExist) {
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
                    successful = true,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}

fun Route.unfollowUser(followService: FollowService) {

    delete("/api/following/unfollow") {
        val request = call.receiveNullable<FollowUpdateRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }
        val didUserExist = followService.unfollowUserIfExists(request)
        if(didUserExist) {
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
                    successful = true,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}
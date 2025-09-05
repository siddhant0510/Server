package Server.skcoding.routes

import Server.skcoding.service.ActivityService
import Server.skcoding.service.PostService
import Server.skcoding.util.Constants
import Server.skcoding.util.QueryParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlin.text.toIntOrNull

fun Route.getActivities(
    activityService: ActivityService
) {
    authenticate {
        get("/api/activity/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val activities = activityService.getActivitiesForUser(call.userId, page, pageSize)
            call.respond(HttpStatusCode.OK, activities)
        }
    }
}
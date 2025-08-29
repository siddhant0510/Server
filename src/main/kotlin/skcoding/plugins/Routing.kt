package Server.skcoding.plugins

import Server.skcoding.routes.createPostRoute
import Server.skcoding.routes.followUser
import Server.skcoding.routes.loginUser
import Server.skcoding.routes.unfollowUser
import Server.skcoding.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // User routes
        userRoutes()
        loginUser()

        // Following routes
        followUser()
        unfollowUser()

        // Post routes
        createPostRoute()
    }
}

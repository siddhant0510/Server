package Server.skcoding.plugins

import Server.skcoding.data.repository.follow.FollowRepository
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.repository.user.UserRepository
import Server.skcoding.routes.createPostRoute
import Server.skcoding.routes.followUser
import Server.skcoding.routes.loginUser
import Server.skcoding.routes.unfollowUser
import Server.skcoding.routes.userRoutes
import Server.skcoding.service.UserService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val userService: UserService by inject()
    val followRepository: FollowRepository by inject()
    val postRepository: PostRepository by inject()
    routing {
        // User routes
        userRoutes(userService)
        loginUser(userRepository)

        // Following routes
        followUser(followRepository)
        unfollowUser(followRepository)

        // Post routes
        createPostRoute(postRepository)
    }
}

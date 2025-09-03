package Server.skcoding.plugins

import Server.skcoding.data.repository.follow.FollowRepository
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.repository.user.UserRepository
import Server.skcoding.routes.createPostRoute
import Server.skcoding.routes.deletePost
import Server.skcoding.routes.followUser
import Server.skcoding.routes.getPostsForFollows
import Server.skcoding.routes.loginUser
import Server.skcoding.routes.unfollowUser
import Server.skcoding.routes.userRoutes
import Server.skcoding.service.FollowService
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        userRoutes(userService)
        loginUser(
            userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        // Following routes
        followUser(followService)
        unfollowUser(followService)

        // Post routes
        createPostRoute(postService, userService)
        getPostsForFollows(postService, userService)
        deletePost(postService, userService)
    }
}

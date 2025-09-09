package Server.skcoding.plugins

import Server.skcoding.routes.createComment
import Server.skcoding.routes.createPost
import Server.skcoding.routes.deletePost
import Server.skcoding.routes.followUser
import Server.skcoding.routes.getPostsForFollows
import Server.skcoding.routes.likeParent
import Server.skcoding.routes.loginUser
import Server.skcoding.routes.unfollowUser
import Server.skcoding.routes.createUser
import Server.skcoding.routes.deleteComment
import Server.skcoding.routes.getActivities
import Server.skcoding.routes.getCommentsForPost
import Server.skcoding.routes.getLikesForParent
import Server.skcoding.routes.getUserProfile
import Server.skcoding.routes.searchUser
import Server.skcoding.routes.unlikeParent
import Server.skcoding.routes.updateUserProfile
import Server.skcoding.service.ActivityService
import Server.skcoding.service.CommentService
import Server.skcoding.service.FollowService
import Server.skcoding.service.LikeService
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import io.ktor.server.application.*
import io.ktor.server.http.content.files
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticResources
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import kotlin.getValue

fun Application.configureRouting() {
    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    routing {
        // User routes
        createUser(userService)
        loginUser(
            userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )
        searchUser(userService)
        getUserProfile(userService)
        getPostsForFollows(postService)
        updateUserProfile(userService)

        // Following routes
        followUser(followService, activityService)
        unfollowUser(followService)

        // Post routes
        createPost(postService)
        getPostsForFollows(postService)
        deletePost(postService, likeService, commentService)

        // Like routes
        likeParent(likeService, activityService)
        unlikeParent(likeService)
        getLikesForParent(likeService)

        // Comment routes
        createComment(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentsForPost(commentService)

        // Activity routes
        getActivities(activityService)

        staticResources("/static", "static")
    }
}

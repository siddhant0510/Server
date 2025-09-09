package Server.skcoding.routes

import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.data.requests.DeletePostRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.service.CommentService
import Server.skcoding.service.LikeService
import Server.skcoding.service.PostService
import Server.skcoding.service.UserService
import Server.skcoding.util.ApiResponseMessages
import Server.skcoding.util.Constants
import Server.skcoding.util.Constants.BASE_URL
import Server.skcoding.util.Constants.PROFILE_PICTURE_PATH
import Server.skcoding.util.QueryParams
import Server.skcoding.util.save
import com.google.gson.Gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import java.io.File

fun Route.createPost(
    postService: PostService,
    userService: UserService
) {
    val gson by inject<Gson>()
    authenticate {
        post("/api/post/create"){
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when(partData) {
                    is PartData.FormItem -> {
                        if(partData.name == "post_data") {
                            createPostRequest = gson.fromJson(
                                partData.value,
                                CreatePostRequest::class.java
                            )
                        }
                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(Constants.POST_PICTURE_PATH)
                    }
                    is PartData.BinaryChannelItem -> Unit
                    is PartData.BinaryItem -> Unit
                }
            }
            val postPictureUrl = "${BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
                val createPostAcknowledge = postService.createPost(
                    request = request,
                    userId = call.userId,
                    imageUrl = postPictureUrl
                )
                if(createPostAcknowledge) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(
                            successful = true
                        )
                    )
                } else {
                    File("${Constants.PROFILE_PICTURE_PATH}/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
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
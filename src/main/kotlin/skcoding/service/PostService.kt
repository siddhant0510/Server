package Server.skcoding.service

import Server.skcoding.data.models.Post
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.util.Constants

class PostService(
    private val repository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest): Boolean {
        return repository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostsForFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostByFollows(userId, page, pageSize)
    }
}
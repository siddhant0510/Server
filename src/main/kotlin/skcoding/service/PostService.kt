package Server.skcoding.service

import Server.skcoding.data.models.Post
import Server.skcoding.data.repository.post.PostRepository
import Server.skcoding.data.requests.CreatePostRequest
import Server.skcoding.util.Constants

class PostService(
    private val repository: PostRepository
) {
    suspend fun createPostIfUserExists(request: CreatePostRequest, userId: String): Boolean {
        return repository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = userId,
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

    suspend fun getPost(postId: String): Post? = repository.getPost(postId)

    suspend fun deletePost(postId: String) {
        repository.deletePost(postId)
    }
}
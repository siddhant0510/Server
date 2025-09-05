package Server.skcoding.service

import Server.skcoding.data.models.Comment
import Server.skcoding.data.repository.comment.CommentRepository
import Server.skcoding.data.requests.CreateCommentRequest
import Server.skcoding.util.Constants

class CommentService(
    private val repository: CommentRepository
) {
    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvent {
        createCommentRequest.apply {
            if(comment.isBlank() || postId.isBlank()){
                return ValidationEvent.ErrorFieldEmpty
            }
            if(comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.ErrorCommentTooLong
            }
        }
        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteCommentsForPost(postId: String) {
        val comments = repository.getCommentsForPost(postId)
        comments.forEach { comments ->
            repository.deleteComment(comments.id)
        }
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String) {
        repository.deleteCommentsFromPost(postId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return repository.getComment(commentId)
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object ErrorCommentTooLong : ValidationEvent()
        object Success : ValidationEvent()
    }
}
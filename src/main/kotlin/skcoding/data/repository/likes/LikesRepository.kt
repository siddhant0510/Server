package Server.skcoding.data.repository.likes

interface LikesRepository {

    suspend fun likeParent(userId: String, parentId: String): Boolean

    suspend fun unlikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParentId(parentId: String)
}
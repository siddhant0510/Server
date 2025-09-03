package Server.skcoding.data.repository.likes

import Server.skcoding.data.models.Like
import Server.skcoding.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikesRepositoryImpl(
    db: CoroutineDatabase
) : LikesRepository {

    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    override suspend fun likeParent(userId: String, parentId: String): Boolean {

        val doesUserExist = users.findOneById(userId) != null
        if(doesUserExist) {
            likes.insertOne(Like(userId, parentId))
            return true
        } else {
            return false
        }
    }

    override suspend fun unlikeParent(userId: String, parentId: String): Boolean {
        val doesUserExist = users.findOneById(userId) != null
        if(doesUserExist) {
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            return true
        } else {
            return false
        }
    }

    override suspend fun deleteLikesForParentId(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId)
    }
}
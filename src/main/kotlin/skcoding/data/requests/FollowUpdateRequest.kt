package Server.skcoding.data.requests

data class FollowUpdateRequest(
    val followingUserId: String,
    val followedUserId: String,
)

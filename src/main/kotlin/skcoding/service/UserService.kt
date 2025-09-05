package Server.skcoding.service

import Server.skcoding.data.models.User
import Server.skcoding.data.repository.follow.FollowRepository
import Server.skcoding.data.repository.user.UserRepository
import Server.skcoding.data.requests.CreateAccountRequest
import Server.skcoding.data.requests.LoginRequest
import Server.skcoding.data.responses.BasicApiResponse
import Server.skcoding.data.responses.UserResponseItem
import Server.skcoding.util.ApiResponseMessages.FIELDS_BLANK
import io.ktor.server.response.respond

class UserService(
    private val repository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun doesUserWithEmailExist(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun doesEmailBelongToUserId(email: String, userId: String): Boolean {
        return repository.doesEmailBelongToUserId(email, userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return repository.getUserByEmail(email)
    }

    fun isValidPassword(enteredPassword: String, actualPassword: String): Boolean {
        return enteredPassword == actualPassword
    }

    suspend fun searchForUsers(query: String, userId: String): List<UserResponseItem> {
        val users = repository.searchForUsers(query)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id} != null
            UserResponseItem(
                username = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )

        }
    }

    suspend fun createUser(request: CreateAccountRequest) {
        repository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                gitHubUrl = null,
                instagramUrl = null,
                linkedInUrl = null
            )
        )
    }
    fun validateCreateAccountRequest(request: CreateAccountRequest): UserService.ValidationEvent {
        if(request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty : ValidationEvent()
        object Success : ValidationEvent()
    }
}
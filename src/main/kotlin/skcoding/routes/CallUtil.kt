package Server.skcoding.routes

import Server.skcoding.plugins.email
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend fun ifEmailBelongsToUser(
    call: ApplicationCall,
    userId: String,
    validateEmail: suspend (email: String, userId: String) -> Boolean,
    onSuccess: suspend () -> Unit
) {
    val isEmailByUser = validateEmail(
        call.principal<JWTPrincipal>()?.email ?: "",
        userId
    )
    if(isEmailByUser) {
        onSuccess()
    } else {
        call.respond(HttpStatusCode.Unauthorized)
    }
}
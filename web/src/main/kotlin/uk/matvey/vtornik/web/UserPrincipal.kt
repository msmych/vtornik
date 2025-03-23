package uk.matvey.vtornik.web

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal

data class UserPrincipal(
    val userId: Int,
    val username: String,
) {

    companion object {

        fun ApplicationCall.userPrincipalOrNull() = principal<UserPrincipal>()

        fun ApplicationCall.userPrincipal() = requireNotNull(userPrincipalOrNull()) {
            "Not found user principal"
        }
    }
}

package uk.matvey.vtornik.web

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal

data class UserPrincipal(
    val id: Int,
    val username: String,
) {

    companion object {

        fun fromDecodedJwt(decoded: DecodedJWT) = UserPrincipal(
            id = decoded.subject.toInt(),
            username = decoded.getClaim("username").asString(),
        )

        fun ApplicationCall.userPrincipalOrNull() = principal<UserPrincipal>()

        fun ApplicationCall.userPrincipal() = requireNotNull(userPrincipalOrNull()) {
            "Not found user principal"
        }
    }
}

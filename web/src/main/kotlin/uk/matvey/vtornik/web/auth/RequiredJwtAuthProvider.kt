package uk.matvey.vtornik.web.auth

import com.auth0.jwt.JWT
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import uk.matvey.vtornik.web.UserPrincipal
import uk.matvey.vtornik.web.config.WebConfig

class RequiredJwtAuthProvider(
    private val config: WebConfig,
) : AuthenticationProvider(Config){

    object Config : AuthenticationProvider.Config("jwt-required")

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val jwt = requireNotNull(context.call.request.cookies["jwt"])
        val decoded = JWT.decode(jwt)
        JWT.require(config.jwtAlgorithm())
            .withIssuer("vtornik")
            .withAudience("vtornik")
            .acceptLeeway(100)
            .build()
            .verify(decoded)
        context.principal(
            UserPrincipal(
                userId = decoded.subject.toInt(),
                username = decoded.getClaim("username").asString(),
            )
        )
    }
}
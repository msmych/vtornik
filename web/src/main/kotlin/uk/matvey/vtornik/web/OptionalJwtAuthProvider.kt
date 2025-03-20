package uk.matvey.vtornik.web

import com.auth0.jwt.JWT
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import uk.matvey.vtornik.web.config.VtornikConfig

class OptionalJwtAuthProvider(
    private val config: VtornikConfig,
) : AuthenticationProvider(Config) {

    object Config : AuthenticationProvider.Config("jwt-optional")

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        context.call.request.cookies["jwt"]?.let { jwt ->
            val decoded = JWT.decode(jwt)
            JWT.require(config.jwtAlgorithm())
                .withIssuer("vtornik")
                .withAudience("vtornik")
                .acceptLeeway(100)
                .build()
                .verify(decoded)
            context.principal(
                UserPrincipal(
                    userId = decoded.subject.toLong(),
                    username = decoded.getClaim("username").asString(),
                    name = decoded.getClaim("name").asString()
                )
            )
        }
    }
}
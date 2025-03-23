package uk.matvey.vtornik.web.auth

import com.auth0.jwt.JWT
import io.ktor.http.CookieEncoding.URI_ENCODING
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import uk.matvey.vtornik.web.UserPrincipal
import uk.matvey.vtornik.web.config.WebConfig
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class Auth(
    private val config: WebConfig,
) {

    val optionalJwtAuthProvider = object : AuthenticationProvider(object : Config(JWT_OPTIONAL) {}) {

        override suspend fun onAuthenticate(context: AuthenticationContext) {
            context.call.request.cookies[JWT_COOKIE]?.let { jwt ->
                processJwt(jwt, context)
            }
        }
    }

    val requiredJwtAuthProvider = object : AuthenticationProvider(object : Config(JWT_REQUIRED) {}) {

        override suspend fun onAuthenticate(context: AuthenticationContext) {
            val jwt = requireNotNull(context.call.request.cookies["jwt"])
            processJwt(jwt, context)
        }
    }

    private fun processJwt(jwt: String, context: AuthenticationContext) {
        val decoded = JWT.decode(jwt)
        JWT.require(config.jwtAlgorithm())
            .withIssuer(JWT_ISSUER)
            .withAudience(JWT_AUDIENCE)
            .acceptLeeway(100)
            .build()
            .verify(decoded)
        context.principal(UserPrincipal.fromDecodedJwt(decoded))
    }

    fun createJwt(
        subjectId: Long,
        username: String,
    ) = JWT.create()
        .withIssuer("vtornik")
        .withAudience("vtornik")
        .withSubject(subjectId.toString())
        .withClaim("username", username)
        .withExpiresAt(Instant.now().plus(7.days.toJavaDuration()))
        .sign(config.jwtAlgorithm())

    fun ApplicationCall.appendJwtCookie(
        subjectId: Long,
        username: String,
    ) {
        response.cookies.append(
            name = "jwt",
            value = createJwt(subjectId, username),
            encoding = URI_ENCODING,
            expires = GMTDate().plus(7.days),
            path = "/",
            httpOnly = true,
            extensions = mapOf(SAMESITE to "Lax"),
        )
    }

    companion object {

        private const val JWT_COOKIE = "jwt"
        private const val JWT_OPTIONAL = "jwt-optional"
        private const val JWT_REQUIRED = "jwt-required"
        private const val JWT_ISSUER = "vtornik"
        private const val JWT_AUDIENCE = "vtornik"

        fun Route.authJwtOptional(block: Route.() -> Unit) = authenticate(JWT_OPTIONAL) { block() }

        fun Route.authJwtRequired(block: Route.() -> Unit) = authenticate(JWT_REQUIRED) { block() }
    }
}
package uk.matvey.vtornik.web.auth

import com.auth0.jwt.JWT
import io.ktor.http.CookieEncoding.URI_ENCODING
import io.ktor.server.application.ApplicationCall
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import uk.matvey.vtornik.web.config.WebConfig
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class Auth(
    private val config: WebConfig,
) {

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
}
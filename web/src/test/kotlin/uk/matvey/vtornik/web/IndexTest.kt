package uk.matvey.vtornik.web

import com.auth0.jwt.JWT
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class IndexTest : WebTestSetup() {

    @Test
    fun `should return index page`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("<html>")
            .contains("<head>")
            .contains("<body>")
    }

    @Test
    fun `should include username if logged in`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/") {
            val jwt = JWT.create()
                .withIssuer("vtornik")
                .withAudience("vtornik")
                .withSubject("1")
                .withClaim("username", "username1")
                .withClaim("name", "Name")
                .withExpiresAt(Instant.now().plus(7.days.toJavaDuration()))
                .sign(config.jwtAlgorithm())
            cookie(
                name = "jwt",
                value = jwt,
                expires = GMTDate() + 7.days,
                path = "/",
                httpOnly = true,
                extensions = mapOf(SAMESITE to "Lax")
            )
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Logged in as")
            .contains("Logout")
    }
}
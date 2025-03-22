package uk.matvey.vtornik.web

import com.auth0.jwt.JWT
import io.ktor.client.request.cookie
import io.ktor.http.HttpMessageBuilder
import io.ktor.server.application.Application
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.mockk.mockk
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestSetup
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.VtornikConfig
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

open class WebTestSetup : PostgresTestSetup() {

    fun HttpMessageBuilder.appendJwtCookie() {
        val jwt = generateJwt()
        cookie(
            name = "jwt",
            value = jwt,
            expires = GMTDate() + 7.days,
            path = "/",
            httpOnly = true,
            extensions = mapOf(SAMESITE to "Lax")
        )
    }

    fun generateJwt(): String = JWT.create()
        .withIssuer("vtornik")
        .withAudience("vtornik")
        .withSubject("1")
        .withClaim("username", "username1")
        .withClaim("name", "Name")
        .withExpiresAt(Instant.now().plus(7.days.toJavaDuration()))
        .sign(config.jwtAlgorithm())

    companion object {

        lateinit var config: VtornikConfig

        @BeforeAll
        @JvmStatic
        fun setupWeb() {
            val flyway = Flyway.configure()
                .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
                .load()
            flyway.migrate()
            config = VtornikConfig(
                appSecret = "app-secret",
                dbUrl = postgres.jdbcUrl,
                dbUsername = postgres.username,
                dbPassword = postgres.password,
                githubClientId = null,
            )
        }
    }

    val tmdbClient = mockk<TmdbClient>()
    val services = Services(tmdbClient, config)

    fun Application.testServerModule() {
        serverModule(
            config = config,
            services = services,
        )
    }
}
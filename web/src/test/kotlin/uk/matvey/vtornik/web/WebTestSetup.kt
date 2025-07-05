package uk.matvey.vtornik.web

import com.auth0.jwt.JWT
import io.ktor.client.request.cookie
import io.ktor.http.HttpMessageBuilder
import io.ktor.server.application.Application
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.mockk.coEvery
import io.mockk.mockk
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestSetup
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.TmdbConfiguration
import uk.matvey.vtornik.web.config.WebConfig
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

open class WebTestSetup : PostgresTestSetup() {

    fun HttpMessageBuilder.appendJwtCookie(userId: Int = Random.nextInt()) {
        val jwt = generateJwt(userId)
        cookie(
            name = "jwt",
            value = jwt,
            expires = GMTDate() + 7.days,
            path = "/",
            httpOnly = true,
            extensions = mapOf(SAMESITE to "Lax")
        )
    }

    fun generateJwt(subjectId: Int): String = JWT.create()
        .withIssuer("vtornik")
        .withAudience("vtornik")
        .withSubject(subjectId.toString())
        .withClaim("username", "username1")
        .withClaim("name", "Name")
        .withExpiresAt(Instant.now().plus(7.days.toJavaDuration()))
        .sign(config.jwtAlgorithm())

    companion object {

        lateinit var config: WebConfig

        val tmdbClient = mockk<TmdbClient>()

        @BeforeAll
        @JvmStatic
        fun setupWeb() {
            val flyway = Flyway.configure()
                .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
                .schemas("vtornik")
                .createSchemas(true)
                .load()
            flyway.migrate()
            config = WebConfig(
                profile = WebConfig.Profile.MOCK,
                appSecret = "appSecret",
                dbUrl = postgres.jdbcUrl,
                dbUsername = postgres.username,
                dbPassword = postgres.password,
                assetsUrl = "assetsUrl",
                githubClientId = null,
                githubClientSecret = null,
                tmdbApiKey = "tmdbApiKey",
            )

            coEvery {
                tmdbClient.getConfiguration()
            } returns TmdbConfiguration(
                images = TmdbConfiguration.Images(
                    baseUrl = "https://image.tmdb.org/t/p/",
                    secureBaseUrl = "https://image.tmdb.org/t/p/",
                    backdropSizes = listOf("w300", "w780", "w1280", "original"),
                    logoSizes = listOf("w45", "w92", "w154", "w185", "w300", "original"),
                    posterSizes = listOf("w92", "w154", "w185", "w342", "w500", "original"),
                    profileSizes = listOf("w45", "h632", "original"),
                    stillSizes = listOf("w300", "original"),
                ),
                changeKeys = listOf(),
            )
        }
    }

    val services = Services(
        config = config,
        tmdbClient = tmdbClient,
    )

    fun Application.testServerModule() {
        serverModule(
            config = config,
            services = services,
        )
    }
}
package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.mockk.mockk
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestSetup
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.VtornikConfig

open class WebTestSetup : PostgresTestSetup() {

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
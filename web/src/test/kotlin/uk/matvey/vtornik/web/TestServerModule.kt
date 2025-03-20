package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.mockk.mockk
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.VtornikConfig

object TestServerModule {

    val config = VtornikConfig(
        appSecret = "app-secret",
    )

    val tmdbClient = mockk<TmdbClient>()

    fun Application.testServerModule() {
        serverModule(
            config,
            tmdbClient,
        )
    }
}
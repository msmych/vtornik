package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.mockk.mockk
import uk.matvey.tmdb.TmdbClient

object TestServerModule {

    val tmdbClient = mockk<TmdbClient>()

    fun Application.testServerModule() = serverModule(
        tmdbClient,
    )
}
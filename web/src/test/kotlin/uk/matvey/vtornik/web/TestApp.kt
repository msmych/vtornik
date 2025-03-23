package uk.matvey.vtornik.web

import io.mockk.coEvery
import io.mockk.mockk
import org.flywaydb.core.Flyway
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.WebConfig

fun main() {
    val config = WebConfig(
        profile = WebConfig.Profile.MOCK,
        appSecret = "appSecret",
        dbUrl = System.getenv("DB_URL"),
        dbUsername = System.getenv("DB_USERNAME"),
        dbPassword = System.getenv("DB_PASSWORD"),
        githubClientId = "githubClientId"
    )
    val tmdbClient = mockk<TmdbClient>()
    coEvery { tmdbClient.searchMovies("dune") } returns TmdbClient.SearchMovieResponse(
        page = 1,
        results = listOf(
            TmdbClient.SearchMovieResponse.ResultItem(1, "Dune", "2020-09-09"),
            TmdbClient.SearchMovieResponse.ResultItem(2, "Dune 2", "2024-09-09"),
        ),
        totalPages = 1,
        totalResults = 2,
    )
    coEvery { tmdbClient.getMovieDetails(1) } returns TmdbClient.MovieDetailsResponse(
        id = 1,
        overview = "Dune overview",
        title = "Dune",
        releaseDate = "2020-09-09",
    )
    val services = Services(config, tmdbClient)
    val flyway = Flyway.configure()
        .dataSource(config.dbUrl, config.dbUsername, config.dbPassword)
        .cleanDisabled(false)
        .load()
    flyway.clean()
    flyway.migrate()
    val server = server(config, services)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}
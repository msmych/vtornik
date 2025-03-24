package uk.matvey.vtornik.web

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.util.getOrFail
import kotlinx.serialization.json.Json
import org.flywaydb.core.Flyway
import uk.matvey.slon.random.randomSentence
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.aMovieDetailsResponse
import uk.matvey.tmdb.aSearchMovieResponse
import uk.matvey.tmdb.aSearchMovieResponseResultItem
import uk.matvey.vtornik.web.config.WebConfig
import kotlin.random.Random

fun main() {
    val config = WebConfig(
        profile = WebConfig.Profile.MOCK,
        appSecret = "appSecret",
        dbUrl = System.getenv("DB_URL"),
        dbUsername = System.getenv("DB_USERNAME"),
        dbPassword = System.getenv("DB_PASSWORD"),
        githubClientId = "githubClientId"
    )
    val tmdbEngine = MockEngine { request ->
        if (request.url.toString().contains("https://api.themoviedb.org/3/search/movie?query=")) {
            respond(
                content = Json.encodeToString(
                    aSearchMovieResponse(
                        (0..<Random.nextInt(16))
                            .map { aSearchMovieResponseResultItem(title = request.url.parameters.getOrFail("query") + " " + randomSentence()) })
                ),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", "application/json"),
            )
        } else if (request.url.toString().contains("https://api.themoviedb.org/3/movie/")) {
            respond(
                content = Json.encodeToString(
                    aMovieDetailsResponse(id = request.url.segments.last().toLong())
                ),
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", "application/json"),
            )
        } else {
            respond(content = "", status = HttpStatusCode.NotFound)
        }
    }
    val tmdbClient = TmdbClient(tmdbEngine)
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
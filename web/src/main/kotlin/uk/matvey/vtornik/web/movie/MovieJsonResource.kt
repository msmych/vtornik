package uk.matvey.vtornik.web.movie

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import uk.matvey.slon.ktor.Resource
import uk.matvey.vtornik.web.Services

class MovieJsonResource(
    services: Services,
) : Resource {

    private val movieService = services.movieService

    override fun Route.routing() {
        route("/movies") {
            route("/now-playing") {
                get {
                    val movies = movieService.getNowPlaying()
                    call.respondText(ContentType.Application.Json) {
                        Json.encodeToString(movies)
                    }
                }
            }
            route("/upcoming") {
                get {
                    val movies = movieService.getUpcoming()
                    call.respondText(ContentType.Application.Json) {
                        Json.encodeToString(movies)
                    }
                }
            }
        }
    }
}
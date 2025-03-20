package uk.matvey.vtornik.web

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.p
import uk.matvey.tmdb.TmdbClient

fun Route.movieRouting(tmdbClient: TmdbClient) {
    route("/movies") {
        route("/{id}") {
            get {
                val id = call.parameters.getOrFail("id")
                val movieDetails = tmdbClient.getMovieDetails(id.toLong())
                call.respondHtml {
                    body {
                        h1 {
                            +movieDetails.title
                        }
                        p {
                            +movieDetails.overview
                        }
                    }
                }
            }
        }
    }
}
package uk.matvey.vtornik.web.search

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import uk.matvey.tmdb.TmdbClient

fun Route.searchRouting(
    tmdbClient: TmdbClient,
) {
    route("/search") {
        get {
            val q = call.parameters.getOrFail("q")
            val movies = tmdbClient.searchMovies(q)
            call.respondHtml {
                body {
                    movies.results.forEach {
                        p {
                            a {
                                href = "/html/movies/${it.id}"
                                attributes["hx-boost"] = "true"
                                +it.title
                                it.releaseDate()?.let { releaseDate -> +" ($releaseDate)" }
                            }
                        }
                    }
                }
            }
        }
    }
}
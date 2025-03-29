package uk.matvey.vtornik.web.person

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import uk.matvey.tmdb.TmdbClient

fun Route.personRouting(tmdbClient: TmdbClient) {
    route("/people") {
        get {
            val movieId = call.pathParameters.getOrFail("id").toLong()
            val role = call.parameters.getOrFail("role")
            val people = tmdbClient.getMovieCredits(movieId)
                .crew
                .filter { it.job == role }
                .joinToString { it.name }
            call.respondHtml {
                body {
                    +"Directed by "
                    +people
                }
            }
        }
    }
}
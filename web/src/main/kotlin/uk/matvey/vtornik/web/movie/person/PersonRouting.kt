package uk.matvey.vtornik.web.movie.person

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
            val movieId = call.pathParameters.getOrFail("movieId").toLong()
            val role = call.parameters.getOrFail("role")
            val people = tmdbClient.getMovieCredits(movieId)
                .crew
                .filter { it.job == role }
            val peopleText = people
                .take(3)
                .joinToString { it.name }
            call.respondHtml {
                body {
                    if (peopleText.isNotEmpty()) {
                        +"Directed by "
                        +peopleText
                        if (people.size > 3) {
                            +" and ${people.size - 3} more"
                        }
                    }
                }
            }
        }
    }
}
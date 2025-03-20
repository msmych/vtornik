package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.search.searchRouting

fun Application.serverModule(tmdbClient: TmdbClient) {
    routing {
        get("/health") {
            call.respondText("OK")
        }
        val githubClientId = System.getenv("GITHUB_CLIENT_ID")
        indexRouting(githubClientId)
        githubClientId?.let {
            githubRouting(it)
        }
        route("/html") {
            searchRouting(tmdbClient)
        }
    }
}

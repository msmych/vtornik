package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.date.GMTDate
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.VtornikConfig
import uk.matvey.vtornik.web.search.searchRouting

fun Application.serverModule(config: VtornikConfig, tmdbClient: TmdbClient) {
    val appSecret = System.getenv("APP_SECRET")
    val optionalJwtAuthProvider = OptionalJwtAuthProvider(config)
    install(Authentication) {
        register(optionalJwtAuthProvider)
    }
    routing {
        get("/health") {
            call.respondText("OK")
        }
        val githubClientId = System.getenv("GITHUB_CLIENT_ID")
        indexRouting(githubClientId)
        githubClientId?.let {
            githubRouting(it, appSecret)
        }
        get("/logout") {
            call.response.cookies.append(name = "jwt", value = "", expires = GMTDate.START)
            call.respondRedirect("/")
        }
        route("/html") {
            searchRouting(tmdbClient)
            movieRouting(tmdbClient)
        }
    }
}

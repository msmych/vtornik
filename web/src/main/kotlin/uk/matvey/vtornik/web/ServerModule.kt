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
import uk.matvey.vtornik.web.auth.OptionalJwtAuthProvider
import uk.matvey.vtornik.web.auth.RequiredJwtAuthProvider
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.movieRouting
import uk.matvey.vtornik.web.movie.tagRouting
import uk.matvey.vtornik.web.search.searchRouting

fun Application.serverModule(config: WebConfig, services: Services) {
    val appSecret = System.getenv("APP_SECRET")
    val optionalJwtAuthProvider = OptionalJwtAuthProvider(config)
    val requiredJwtAuthProvider = RequiredJwtAuthProvider(config)
    install(Authentication) {
        register(optionalJwtAuthProvider)
        register(requiredJwtAuthProvider)
    }
    routing {
        get("/health") {
            call.respondText("OK")
        }
        val githubClientId = System.getenv("GITHUB_CLIENT_ID")
        indexRouting(config)
        githubClientId?.let {
            githubRouting(it, appSecret, services.userRepository)
        }
        if (config.profile == WebConfig.Profile.MOCK) {
            route("/mock") {
                get("/login") {
                    with(services.auth) {
                        call.appendJwtCookie(1, "tester")
                        call.respondRedirect("/")
                    }
                }
            }
        }
        get("/logout") {
            call.response.cookies.append(name = "jwt", value = "", expires = GMTDate.START)
            call.respondRedirect("/")
        }
        route("/html") {
            searchRouting(services.tmdbClient)
            movieRouting(
                config,
                services.tmdbClient,
                services.movieRepository,
                services.tagRepository
            )
            tagRouting(services.tagRepository)
        }
    }
}

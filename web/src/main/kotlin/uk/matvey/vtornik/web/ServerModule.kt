package uk.matvey.vtornik.web

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.date.GMTDate
import uk.matvey.vtornik.web.auth.Auth.Companion.JWT_COOKIE
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile
import uk.matvey.vtornik.web.movie.MovieResource

fun Application.serverModule(config: WebConfig, services: Services) {
    install(Authentication) {
        register(services.auth.optionalJwtAuthProvider)
        register(services.auth.requiredJwtAuthProvider)
    }
    routing {
        staticResources("/assets", "/assets")
        get("/health") {
            call.respondText("OK")
        }
        indexRouting(config)
        config.githubClientId?.let {
            githubRouting(config, services.auth, services.userRepository)
        }
        if (config.profile == Profile.MOCK) {
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
            call.response.cookies.append(name = JWT_COOKIE, value = "", expires = GMTDate.START)
            call.respondRedirect("/")
        }
        route("/html") {
            with(
                MovieResource(
                    config = config,
                    movieService = services.movieService,
                    movieRepository = services.movieRepository,
                    personRepository = services.personRepository,
                    tagRepository = services.tagRepository,
                    noteRepository = services.noteRepository,
                    tmdbClient = services.tmdbClient,
                    tmdbImages = services.tmdbImages,
                )
            ) {
                routing()
            }
        }
    }
}

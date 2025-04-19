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
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile
import uk.matvey.vtornik.web.movie.movieRouting

fun Application.serverModule(config: WebConfig, services: Services) {
    val appSecret = System.getenv("APP_SECRET")
    install(Authentication) {
        register(services.auth.optionalJwtAuthProvider)
        register(services.auth.requiredJwtAuthProvider)
    }
    routing {
        staticResources("/assets", "/assets")
        get("/health") {
            call.respondText("OK")
        }
        val githubClientId = System.getenv("GITHUB_CLIENT_ID")
        indexRouting(config)
        githubClientId?.let {
            githubRouting(it, appSecret, services.userRepository)
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
            call.response.cookies.append(name = "jwt", value = "", expires = GMTDate.START)
            call.respondRedirect("/")
        }
        route("/html") {
            movieRouting(
                config = config,
                movieService = services.movieService,
                movieRepository = services.movieRepository,
                personRepository = services.personRepository,
                tagRepository = services.tagRepository,
                tmdbClient = services.tmdbClient,
                tmdbImages = services.tmdbImages,
            )
        }
    }
}

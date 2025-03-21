package uk.matvey.vtornik.web

import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.indexRouting(githubClientId: String?) {
    authenticate("jwt-optional") {
        get {
            val principal = call.principal<UserPrincipal>()
            call.respondHtml {
                page(principal, githubClientId) {
                }
            }
        }
    }
}

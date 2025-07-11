package uk.matvey.vtornik.web

import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.p
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.auth.UserPrincipal
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.page.page

fun Routing.indexRouting(config: WebConfig) {
    authJwtOptional {
        get {
            val principal = call.principal<UserPrincipal>()
            call.respondHtml {
                page(config, principal, "Vtornik", call.request, "vtornik") {
                    p {
                        +"Search movie by title, or view your collections"
                    }
                }
            }
        }
    }
}

package uk.matvey.vtornik.web.movie

import io.ktor.server.auth.authenticate
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.button
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipal

fun Route.tagRouting(tagRepository: TagRepository) {
    authenticate("jwt-required") {
        route("/movies/{id}/tags/{tag}") {
            post {
                val principal = call.userPrincipal()
                val id = call.parameters.getOrFail("id").toLong()
                val tag = call.parameters.getOrFail("tag")
                tagRepository.add(principal.userId, id, tag)
                call.respondHtml {
                    body {
                        button {
                            attributes["hx-delete"] = "/html/movies/$id/tags/$tag"
                            attributes["hx-swap"] = "outerHTML"
                            +"Remove from "
                            +tag
                        }
                    }
                }
            }
            delete {
                val principal = call.userPrincipal()
                val id = call.parameters.getOrFail("id").toLong()
                val tag = call.parameters.getOrFail("tag")
                tagRepository.delete(principal.userId, id, tag)
                call.respondHtml {
                    body {
                        button {
                            attributes["hx-post"] = "/html/movies/$id/tags/$tag"
                            attributes["hx-swap"] = "outerHTML"
                            +"Add to "
                            +tag
                        }
                    }
                }
            }
        }
    }
}
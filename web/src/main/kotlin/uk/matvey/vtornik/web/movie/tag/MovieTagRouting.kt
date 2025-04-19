package uk.matvey.vtornik.web.movie.tag

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtRequired
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS

fun Route.movieTagRouting(tagRepository: TagRepository) {
    authJwtRequired {
        route("/tags") {
            route("/{tag}") {
                post {
                    val principal = call.userPrincipal()
                    val movieId = call.parameters.getOrFail("movieId").toLong()
                    val tag = call.parameters.getOrFail("tag")
                    tagRepository.add(principal.id, movieId, tag)
                    call.respondHtml {
                        body {
                            tagToggle(movieId, STANDARD_TAGS.first { it.tag == tag}, true)
                        }
                    }
                }
                delete {
                    val principal = call.userPrincipal()
                    val movieId = call.parameters.getOrFail("movieId").toLong()
                    val tag = call.parameters.getOrFail("tag")
                    tagRepository.delete(principal.id, movieId, tag)
                    call.respondHtml {
                        body {
                            tagToggle(movieId, STANDARD_TAGS.first { it.tag == tag}, false)
                        }
                    }
                }
            }
        }
    }
}
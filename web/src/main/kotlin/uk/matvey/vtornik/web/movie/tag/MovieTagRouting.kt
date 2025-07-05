package uk.matvey.vtornik.web.movie.tag

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.serialization.json.JsonPrimitive
import uk.matvey.vtornik.tag.Tag
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
                    val type = Tag.Type.valueOf(call.parameters.getOrFail("tag").uppercase())
                    tagRepository.set(principal.id, movieId, type, JsonPrimitive(true))
                    call.respondHtml {
                        body {
                            tagToggle(movieId, STANDARD_TAGS.first { it.tag == type.name.lowercase() }, true)
                        }
                    }
                }
                delete {
                    val principal = call.userPrincipal()
                    val movieId = call.parameters.getOrFail("movieId").toLong()
                    val type = Tag.Type.valueOf(call.parameters.getOrFail("tag").uppercase())
                    tagRepository.set(principal.id, movieId, type, JsonPrimitive(false))
                    call.respondHtml {
                        body {
                            tagToggle(movieId, STANDARD_TAGS.first { it.tag == type.name.lowercase() }, false)
                        }
                    }
                }
            }
        }
    }
}
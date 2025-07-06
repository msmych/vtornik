package uk.matvey.vtornik.web.movie.tag

import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import uk.matvey.slon.ktor.Resource
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtRequired
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS

class TagResource(private val tagRepository: TagRepository) : Resource {

    override fun Route.routing() {
        authJwtRequired {
            route("/tags") {
                route("/{type}") {
                    put {
                        val principal = call.userPrincipal()
                        val movieId = call.parameters.getOrFail("movieId").toLong()
                        val type = Tag.Type.valueOf(call.parameters.getOrFail("type"))
                        val value = Json.parseToJsonElement(call.receiveParameters().getOrFail("value"))
                        tagRepository.set(principal.id, movieId, type, value)
                        call.respondHtml {
                            body {
                                tagToggle(
                                    movieId,
                                    STANDARD_TAGS.first { it.tag == type.name },
                                    value.jsonPrimitive.boolean
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package uk.matvey.vtornik.web.movie.tag

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.button
import uk.matvey.slon.html.hxDelete
import uk.matvey.slon.html.hxPost
import uk.matvey.slon.html.hxSwap
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtRequired

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
                            button {
                                hxDelete("/html/movies/$movieId/tags/$tag")
                                hxSwap("outerHTML")
                                +"Remove from "
                                +tag
                            }
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
                            button {
                                hxPost("/html/movies/$movieId/tags/$tag")
                                hxSwap("outerHTML")
                                +"Add to "
                                +tag
                            }
                        }
                    }
                }
            }
        }
    }
}
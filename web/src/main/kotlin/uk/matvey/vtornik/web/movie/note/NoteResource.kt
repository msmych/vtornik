package uk.matvey.vtornik.web.movie.note

import io.ktor.htmx.HxSwap.outerHtml
import io.ktor.htmx.html.hx
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.submitInput
import kotlinx.html.textArea
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import uk.matvey.slon.ktor.Resource
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtRequired
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal

class NoteResource(
    private val tagRepository: TagRepository,
) : Resource {

    override fun Route.routing() {
        authJwtRequired {
            route("/notes") {
                getMovieNotes(tagRepository)
                route("/edit") {
                    getEditNote(tagRepository)
                    postEditNote(tagRepository)
                }
            }
        }
    }

    private fun Route.postEditNote(tagRepository: TagRepository) {
        post {
            val params = call.receiveParameters()
            val text = params.getOrFail("note")
            val principal = call.userPrincipal()
            val movieId = call.pathParameters.getOrFail("movieId").toLong()
            tagRepository.set(principal.id, movieId, Tag.Type.NOTE, JsonPrimitive(text))
            call.respondHtml {
                body {
                    noteText(movieId, text)
                }
            }
        }
    }

    private fun Route.getEditNote(tagRepository: TagRepository) {
        get {
            val principal = call.userPrincipal()
            val movieId = call.pathParameters.getOrFail("movieId").toLong()
            val tag = tagRepository.findByUserIdMovieIdAndType(principal.id, movieId, Tag.Type.NOTE)
            call.respondHtml {
                body {
                    noteForm(movieId, tag)
                }
            }
        }
    }

    private fun Route.getMovieNotes(tagRepository: TagRepository) {
        get {
            val principal = call.userPrincipal()
            val movieId = call.pathParameters.getOrFail("movieId").toLong()
            tagRepository.findByUserIdMovieIdAndType(principal.id, movieId, Tag.Type.NOTE)?.let { note ->
                call.respondHtml {
                    body {
                        noteText(movieId, note.value.jsonPrimitive.content)
                    }
                }
            } ?: call.respondHtml {
                body {
                    noteForm(movieId, null)
                }
            }
        }
    }

    @OptIn(ExperimentalKtorApi::class)
    private fun HtmlBlockTag.noteText(movieId: Long, text: String) {
        div("col gap-8") {
            div {
                +text
            }
            button {
                attributes.hx {
                    get = "/html/movies/$movieId/notes/edit"
                    target = "closest .col"
                    swap = outerHtml
                }
                +"Edit"
            }
        }
    }

    @OptIn(ExperimentalKtorApi::class)
    private fun HtmlBlockTag.noteForm(movieId: Long, tag: Tag?) {
        form(classes = "col gap-8") {
            attributes.hx {
                post = "/html/movies/$movieId/notes/edit"
                swap = outerHtml
            }
            textArea {
                name = "note"
                rows = "8"
                maxLength = "4096"
                +(tag?.value?.jsonPrimitive?.content ?: "")
            }
            submitInput {
            }
        }
    }
}

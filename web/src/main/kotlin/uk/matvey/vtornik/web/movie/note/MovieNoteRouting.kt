package uk.matvey.vtornik.web.movie.note

import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.submitInput
import kotlinx.html.textArea
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxPost
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTarget
import uk.matvey.vtornik.note.Note
import uk.matvey.vtornik.note.NoteRepository
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal

fun Route.movieNoteRouting(
    noteRepository: NoteRepository
) {
    route("/notes") {
        getMovieNotes(noteRepository)
        route("/edit") {
            getEditNote(noteRepository)
            postEditNote(noteRepository)
        }
    }
}

private fun Route.postEditNote(noteRepository: NoteRepository) {
    post {
        val params = call.receiveParameters()
        val text = params.getOrFail("note")
        val principal = call.userPrincipal()
        val movieId = call.pathParameters.getOrFail("movieId").toLong()
        val note = noteRepository.upsert(movieId, principal.id, text)
        call.respondHtml {
            body {
                noteText(movieId, note)
            }
        }
    }
}

private fun Route.getEditNote(noteRepository: NoteRepository) {
    get {
        val principal = call.userPrincipal()
        val movieId = call.pathParameters.getOrFail("movieId").toLong()
        val note = noteRepository.findByMovieAndUser(movieId, principal.id)
        call.respondHtml {
            body {
                noteForm(movieId, note)
            }
        }
    }
}

private fun Route.getMovieNotes(noteRepository: NoteRepository) {
    get {
        val principal = call.userPrincipal()
        val movieId = call.pathParameters.getOrFail("movieId").toLong()
        noteRepository.findByMovieAndUser(movieId, principal.id)?.let { note ->
            call.respondHtml {
                body {
                    noteText(movieId, note)
                }
            }
        } ?: call.respondHtml {
            body {
                noteForm(movieId, null)
            }
        }
    }
}

private fun HtmlBlockTag.noteText(movieId: Long, note: Note) {
    div("col gap-8") {
        div {
            +note.note
        }
        button {
            hxGet("/html/movies/$movieId/notes/edit")
            hxTarget("closest .col")
            hxSwap("outerHTML")
            +"Edit"
        }
    }
}

private fun HtmlBlockTag.noteForm(movieId: Long, note: Note?) {
    form(classes = "col gap-8") {
        hxPost("/html/movies/$movieId/notes/edit")
        hxSwap("outerHTML")
        textArea {
            name = "note"
            maxLength = "4096"
            +(note?.note ?: "")
        }
        submitInput {
        }
    }
}


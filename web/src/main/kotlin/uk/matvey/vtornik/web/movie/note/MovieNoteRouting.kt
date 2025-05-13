package uk.matvey.vtornik.web.movie.note

import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.p
import kotlinx.html.submitInput
import kotlinx.html.textArea
import kotlinx.serialization.json.put
import uk.matvey.slon.html.hxDelete
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxPost
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTarget
import uk.matvey.slon.html.hxVals
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.note.Note
import uk.matvey.vtornik.note.NoteRepository
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal

fun Route.movieNoteRouting(
    movieRepository: MovieRepository,
    noteRepository: NoteRepository
) {
    route("/notes") {
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
        route("/edit") {
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
        route("/add") {
            get {
                val movieId = call.pathParameters.getOrFail("movieId").toLong()
                call.respondHtml {
                    body {
                        form("col gap-8") {
                            hxPost("/html/movies/$movieId/mentions/add")
                            hxSwap("outerHTML")
                            hxVals {
                                put("movieId", movieId)
                            }
                            div {
                                label {
                                    +"Link"
                                    input {
                                        name = "link"
                                    }
                                }
                            }
                            div {
                                label {
                                    +"Name"
                                    input {
                                        name = "name"
                                    }
                                }
                            }
                            div("row gap-8") {
                                submitInput {
                                }
                                button {
                                    hxDelete("/html/movies/$movieId/mentions/add")
                                    hxTarget("closest form")
                                    hxSwap("outerHTML")
                                    +"Cancel"
                                }
                            }
                        }
                    }
                }
            }
            post {
                val params = call.receiveParameters()
                val movieId = params.getOrFail("movieId").toLong()
                val link = params.getOrFail("link")
                val name = params.getOrFail("name")
                val movie = movieRepository.getById(movieId)
                    .addMention(link, name)
                movieRepository.update(movie)
                call.respondHtml {
                    body {
                        p {
                            a {
                                href = link
                                target = "_blank"
                                +name
                            }
                        }
                        addMovieMentionButton(movieId)
                    }
                }
            }
            delete {
                call.respondHtml {
                    body {
                        val movieId = call.pathParameters.getOrFail("movieId").toLong()
                        addMovieMentionButton(movieId)
                    }
                }
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
    form("col gap-8") {
        hxPost("/html/movies/$movieId/notes/edit")
        hxSwap("outerHTML")
        div {
            textArea {
                name = "note"
                maxLength = "4096"
                +(note?.note ?: "")
            }
        }
        div {
            submitInput {
            }
        }
    }
}

fun HtmlBlockTag.addMovieMentionButton(movieId: Long) {
    div {
        button {
            hxGet("/html/movies/$movieId/mentions/add")
            hxSwap("outerHTML")
            +"Add"
        }
    }
}
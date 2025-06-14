package uk.matvey.vtornik.web.movie

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.dialog
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.title
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTrigger
import uk.matvey.slon.ktor.Resource
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.TmdbImages
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.note.NoteRepository
import uk.matvey.vtornik.person.PersonRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipalOrNull
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.note.movieNoteRouting
import uk.matvey.vtornik.web.movie.person.personRouting
import uk.matvey.vtornik.web.movie.search.MovieSearchResource
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS
import uk.matvey.vtornik.web.movie.tag.movieTagRouting
import uk.matvey.vtornik.web.movie.tag.tagToggle
import uk.matvey.vtornik.web.page.page

class MovieResource(
    private val config: WebConfig,
    private val movieService: MovieService,
    private val movieRepository: MovieRepository,
    private val personRepository: PersonRepository,
    private val tagRepository: TagRepository,
    private val noteRepository: NoteRepository,
    private val tmdbClient: TmdbClient,
    private val tmdbImages: TmdbImages,
) : Resource {

    override fun Route.routing() {
        authJwtOptional {
            route("/movies") {
                with(
                    MovieSearchResource(
                        config = config,
                        tmdbClient = tmdbClient,
                        tagRepository = tagRepository,
                        movieRepository = movieRepository,
                        personRepository = personRepository,
                        tmdbImages = tmdbImages,
                        noteRepository = noteRepository,
                    )
                ) {
                    routing()
                }
                route("/now-playing") {
                    getNowPlaying()
                    route("/content") {
                        getNowPlayingContent()
                    }
                }
                route("/upcoming") {
                    getUpcoming()
                    route("/content") {
                        getUpcomingContent()
                    }
                }
                route("/{movieId}") {
                    movieTagRouting(
                        tagRepository = tagRepository,
                    )
                    getMovieDetails()
                    personRouting(tmdbClient)
                    movieNoteRouting(noteRepository)
                }
            }
        }
    }

    private fun Route.getNowPlaying() {
        get {
            val principal = call.userPrincipalOrNull()
            call.respondHtml {
                page(config, principal, "Now playing", "now-playing") {
                    h3 {
                        +"Now playing"
                    }
                    div("row wrap") {
                        hxGet("/html/movies/now-playing/content")
                        hxTrigger("load")
                        div(HTMX_INDICATOR) {
                            +"\uD83C\uDF7F"
                        }
                    }
                }
            }
        }
    }

    private fun Route.getNowPlayingContent() {
        get {
            val nowPlaying = tmdbClient.nowPlayingMovies()
            call.respondHtml {
                body {
                    nowPlaying.results.forEach { movie ->
                        movieCardHtml(
                            movie = MovieCard(movie.id, movie.title, movie.posterPath),
                            tmdbImages = tmdbImages,
                            config = config,
                        )
                    }
                }
            }
        }
    }

    private fun Route.getUpcoming() {
        get {
            val principal = call.userPrincipalOrNull()
            call.respondHtml {
                page(config, principal, "Upcoming", "upcoming") {
                    h3 {
                        +"Upcoming"
                    }
                    div(classes = "row wrap") {
                        hxGet("/html/movies/upcoming/content")
                        hxTrigger("load")
                    }
                }
            }
        }
    }

    private fun Route.getUpcomingContent() {
        get {
            val upcoming = tmdbClient.upcomingMovies()
            call.respondHtml {
                body {
                    upcoming.results.forEach { movie ->
                        movieCardHtml(
                            movie = MovieCard(movie.id, movie.title, movie.posterPath),
                            tmdbImages = tmdbImages,
                            config = config,
                        )
                    }
                }
            }
        }
    }

    private fun Route.getMovieDetails() {
        get {
            val principal = call.userPrincipalOrNull()
            val id = call.parameters.getOrFail("movieId").toLong()
            val movie = movieService.ensureMovie(id)

            val directors = personRepository.findAllPeopleByMovieId(movie.id, Movie.Role.DIRECTOR)

            val movieTags = principal?.let {
                tagRepository.findAllByUserIdAndMovieId(it.id, movie.id)
            }?.map { it.tag }

            call.respondHtml {
                val principal = call.userPrincipalOrNull()
                page(config, principal, movie.title, "vtornik") {
                    div("row gap-8 movie-page wrap") {
                        img(classes = "poster", alt = movie.title) {
                            src = movie.details.tmdb?.posterPath?.let {
                                tmdbImages.posterUrl(it, "w500")
                            } ?: config.assetUrl("/no-poster.jpg")
                            alt = movie.title
                        }
                        div("movie-details") {
                            h1 {
                                +movie.title
                            }
                            movie.details.tmdb?.originalTitle()?.let {
                                h3 {
                                    i {
                                        +it
                                    }
                                }
                            }
                            movie.releaseDate?.let {
                                h3 {
                                    +"Released in "
                                    +it.year.toString()
                                }
                            }
                            directors.takeIf { it.isNotEmpty() }?.let { dirs ->
                                h3 {
                                    +"Directed by "
                                    dirs.forEach {
                                        a {
                                            hxBoost()
                                            href = "/html/movies/search?director=${it.id}"
                                            +it.name
                                        }
                                        +" "
                                    }
                                }
                            }
                            h3 {
                                +"Run time: "
                                +movie.runtime.toString()
                                +" minutes"
                            }
                            principal?.let {
                                div("row gap-8") {
                                    STANDARD_TAGS.forEach { tag ->
                                        tagToggle(movie.id, tag, movieTags?.contains(tag.tag) == true)
                                    }
                                }
                            }
                            p {
                                +movie.details.tmdb().overview
                            }
                            p {
                                principal?.let {
                                    button {
                                        this.title = "Your private notes"
                                        onClick = "movieNotesDialog.showModal()"
                                        +"Notes"
                                    }
                                    dialog {
                                        this.id = "movieNotesDialog"
                                        attributes["closedby"] = "any"
                                        hxGet("/html/movies/${movie.id}/notes")
                                        hxTrigger("load")
                                        hxSwap("beforeend")
                                        button {
                                            onClick = "movieNotesDialog.close()"
                                            +"Close"
                                        }
                                        h1 {
                                            +"${movie.title} notes"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

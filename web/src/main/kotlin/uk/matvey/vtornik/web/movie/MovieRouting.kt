package uk.matvey.vtornik.web.movie

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.dialog
import kotlinx.html.div
import kotlinx.html.figcaption
import kotlinx.html.figure
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.title
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTrigger
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
import uk.matvey.vtornik.web.movie.search.movieSearchRouting
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS
import uk.matvey.vtornik.web.movie.tag.movieTagRouting
import uk.matvey.vtornik.web.movie.tag.tagToggle
import uk.matvey.vtornik.web.page

fun Route.movieRouting(
    config: WebConfig,
    movieService: MovieService,
    movieRepository: MovieRepository,
    personRepository: PersonRepository,
    tagRepository: TagRepository,
    noteRepository: NoteRepository,
    tmdbClient: TmdbClient,
    tmdbImages: TmdbImages,
) {
    authJwtOptional {
        route("/movies") {
            movieSearchRouting(
                config = config,
                tmdbClient = tmdbClient,
                tagRepository = tagRepository,
                movieRepository = movieRepository,
                personRepository = personRepository,
                tmdbImages = tmdbImages,
            )
            route("/now-playing") {
                getNowPlaying(config, tmdbClient, tmdbImages)
            }
            route("/{movieId}") {
                movieTagRouting(
                    tagRepository = tagRepository,
                )
                getMovieDetails(movieService, personRepository, tagRepository, config, tmdbImages)
                personRouting(tmdbClient)
                movieNoteRouting(noteRepository)
            }
        }
    }
}

private fun Route.getNowPlaying(config: WebConfig, tmdbClient: TmdbClient, tmdbImages: TmdbImages) {
    get {
        val nowPlaying = tmdbClient.nowPlayingMovies()
        val principal = call.userPrincipalOrNull()
        call.respondHtml {
            page(config, principal, "Now playing", "now-playing") {
                h3 {
                    +"Now playing"
                }
                div(classes = "row wrap") {
                    nowPlaying.results.forEach { movie ->
                        a {
                            title = movie.title
                            href = "/html/movies/${movie.id}"
                            figure {
                                img(classes = "poster", alt = movie.title) {
                                    src = movie.posterPath?.let {
                                        tmdbImages.posterUrl(it, "w500")
                                    } ?: config.assetUrl("/no-poster.jpg")
                                    alt = movie.title
                                }
                                figcaption {
                                    +movie.title
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Route.getMovieDetails(
    movieService: MovieService,
    personRepository: PersonRepository,
    tagRepository: TagRepository,
    config: WebConfig,
    tmdbImages: TmdbImages
) {
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
                                    onClick = "openDialog('movie-notes-dialog')"
                                    +"Notes"
                                }
                                dialog {
                                    this.id = "movie-notes-dialog"
                                    attributes["closedby"] = "any"
                                    hxGet("/html/movies/${movie.id}/notes")
                                    hxTrigger("load")
                                    hxSwap("beforeend")
                                    button {
                                        onClick = "closeDialog('movie-notes-dialog')"
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

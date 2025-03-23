package uk.matvey.vtornik.web.movie

import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.button
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.p
import uk.matvey.slon.html.hxDelete
import uk.matvey.slon.html.hxPost
import uk.matvey.slon.html.hxSwap
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipalOrNull
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.page

fun Route.movieRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    movieRepository: MovieRepository,
    tagRepository: TagRepository,
) {
    authJwtOptional {
        route("/movies") {
            route("/{id}") {
                get {
                    val principal = call.userPrincipalOrNull()
                    val id = call.parameters.getOrFail("id").toLong()
                    val movie = movieRepository.findById(id) ?: run {
                        val movieDetails = tmdbClient.getMovieDetails(id)
                        movieRepository.add(
                            Movie.Details.Tmdb(
                                id = id,
                                title = movieDetails.title,
                                overview = movieDetails.overview,
                                releaseDate = movieDetails.releaseDate()?.toString(),
                            )
                        )
                        movieRepository.getById(id)
                    }

                    val movieTags = principal?.let {
                        tagRepository.findAllByUserIdAndMovieId(it.id, movie.id)
                    }?.map { it.tag }

                    call.respondHtml {
                        val principal = call.principal<UserPrincipal>()
                        page(config, principal) {
                            h1 {
                                +movie.title
                            }
                            movie.year?.let {
                                h3 { +it.toString() }
                            }
                            principal?.let {
                                setOf("watchlist", "watched").forEach { tag ->
                                    if (movieTags?.contains(tag) == true) {
                                        button {
                                            hxDelete("/html/movies/${movie.id}/tags/$tag")
                                            hxSwap("outerHTML")
                                            +"Remove from "
                                            +tag
                                        }
                                    } else {
                                        button {
                                            hxPost("/html/movies/${movie.id}/tags/$tag")
                                            hxSwap("outerHTML")
                                            +"Add to "
                                            +tag
                                        }
                                    }
                                }
                            }
                            p {
                                +movie.details.tmdb().overview
                            }
                        }
                    }
                }
            }
        }
    }
}

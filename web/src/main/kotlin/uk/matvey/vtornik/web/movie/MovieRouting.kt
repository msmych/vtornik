package uk.matvey.vtornik.web.movie

import io.ktor.server.auth.authenticate
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
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal
import uk.matvey.vtornik.web.config.VtornikConfig
import uk.matvey.vtornik.web.page

fun Route.movieRouting(
    config: VtornikConfig,
    tmdbClient: TmdbClient,
    movieRepository: MovieRepository,
    tagRepository: TagRepository,
) {
    authenticate("jwt-optional") {
        route("/movies") {
            route("/{id}") {
                get {
                    val principal = call.principal<UserPrincipal>()
                    val id = call.parameters.getOrFail("id").toLong()
                    val movie = movieRepository.findById(id) ?: run {
                        val movieDetails = tmdbClient.getMovieDetails(id)
                        movieRepository.add(
                            movieDetails.title,
                            Movie.Details(
                                tmdb = Movie.Details.Tmdb(
                                    id = id,
                                    overview = movieDetails.overview,
                                    releaseDate = movieDetails.releaseDate()?.toString(),
                                )
                            )
                        )
                        movieRepository.findById(id)!!
                    }

                    val movieTags = principal?.let {
                        tagRepository.findAllByUserIdAndMovieId(it.userId.toInt(), movie.id)
                    }?.map { it.tag }

                    call.respondHtml {
                        val principal = call.principal<UserPrincipal>()
                        page(principal, config.githubClientId) {
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
                                            attributes["hx-delete"] = "/html/movies/${movie.id}/tags/$tag"
                                            attributes["hx-swap"] = "outerHTML"
                                            +"Remove from "
                                            +tag
                                        }
                                    } else {
                                        button {
                                            attributes["hx-post"] = "/html/movies/${movie.id}/tags/$tag"
                                            attributes["hx-swap"] = "outerHTML"
                                            +"Add to "
                                            +tag
                                        }
                                    }
                                }
                            }
                            p {
                                +movie.details.tmdb!!.overview
                            }
                        }
                    }
                }
            }
        }
    }
}

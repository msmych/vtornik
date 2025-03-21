package uk.matvey.vtornik.web

import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.p
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.web.config.VtornikConfig

fun Route.movieRouting(config: VtornikConfig, tmdbClient: TmdbClient, movieRepository: MovieRepository) {
    route("/movies") {
        route("/{id}") {
            get {
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

                call.respondHtml {
                    val principal = call.principal<UserPrincipal>()
                    page(principal, config.githubClientId) {
                        h1 {
                            +movie.title
                        }
                        movie.year?.let {
                            h3 { +it.toString() }
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
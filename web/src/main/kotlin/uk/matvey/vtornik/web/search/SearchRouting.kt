package uk.matvey.vtornik.web.search

import io.ktor.server.auth.authenticate
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.page

fun Route.searchRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    tagRepository: TagRepository,
    movieRepository: MovieRepository,
) {
    authenticate("jwt-optional") {
        route("/movies/search") {
            get {
                call.parameters["q"]?.let { q ->
                    val movies = tmdbClient.searchMovies(q)
                    call.respondHtml {
                        body {
                            movies.results.forEach {
                                p {
                                    a {
                                        href = "/html/movies/${it.id}"
                                        attributes["hx-boost"] = "true"
                                        +it.title
                                        it.releaseDate()?.let { releaseDate -> +" (${releaseDate.year})" }
                                    }
                                }
                            }
                        }
                    }
                } ?: run {
                    val principal = call.userPrincipal()
                    val tag = call.parameters.getOrFail("tag")
                    val tags = tagRepository.findAllByUserAndTag(principal.userId, tag)
                    val movies = movieRepository.findAllByIds(tags.map { it.movieId })
                    call.respondHtml {
                        page(config, principal) {
                            movies.forEach { movie ->
                                p {
                                    a {
                                        href = "/html/movies/${movie.details.tmdb().id}"
                                        attributes["hx-boost"] = "true"
                                        +movie.title
                                        movie.year?.let { +" ($it)" }
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
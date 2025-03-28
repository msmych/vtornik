package uk.matvey.vtornik.web.search

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.p
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxTarget
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.page

fun Route.searchRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    tagRepository: TagRepository,
    movieRepository: MovieRepository,
) {
    authJwtOptional {
        route("/movies/search") {
            get {
                call.parameters["q"]?.let { q ->
                    val movies = tmdbClient.searchMovies(q)
                    call.respondHtml {
                        body {
                            div("col gap-8") {
                                movies.results.forEach { movie ->
                                    div("row gap-8 search-result-item") {
                                        hxGet("/html/movies/${movie.id}")
                                        hxTarget("body")
                                        attributes["hx-push-url"] = "true"
                                        img(classes = "poster", alt = movie.title) {
                                            src = movie.posterUrl() ?: ""
                                        }
                                        div("col gap-8") {
                                            b {
                                                +movie.title
                                                movie.releaseDate()?.let { releaseDate -> +" (${releaseDate.year})" }
                                            }
                                            movie.originalTitle()?.let {
                                                i {
                                                    +it
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } ?: run {
                    val principal = call.userPrincipal()
                    val tag = call.parameters.getOrFail("tag")
                    val tags = tagRepository.findAllByUserAndTag(principal.id, tag)
                    val movies = movieRepository.findAllByIds(tags.map { it.movieId })
                    call.respondHtml {
                        page(config, principal) {
                            movies.forEach { movie ->
                                p {
                                    a {
                                        href = "/html/movies/${movie.details.tmdb().id}"
                                        hxBoost()
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
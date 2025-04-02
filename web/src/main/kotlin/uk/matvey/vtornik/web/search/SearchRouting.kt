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
import kotlinx.serialization.json.put
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxIndicator
import uk.matvey.slon.html.hxPushUrl
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTarget
import uk.matvey.slon.html.hxTrigger
import uk.matvey.slon.html.hxVals
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.PersonRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.UserPrincipal.Companion.userPrincipalOrNull
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.page

fun Route.searchRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    tagRepository: TagRepository,
    movieRepository: MovieRepository,
    personRepository: PersonRepository,
) {
    authJwtOptional {
        route("/movies/search") {
            get {
                call.parameters["q"]?.let { q ->
                    val movies = tmdbClient.searchMovies(q)
                    val directors = personRepository.findAllPeopleByMoviesIds(
                        movies.results.map { it.id.toLong() },
                        Movie.Role.DIRECTOR
                    )
                    call.respondHtml {
                        body {
                            div("col gap-8") {
                                movies.results.forEach { movie ->
                                    div("row gap-8 search-result-item") {
                                        hxGet("/html/movies/${movie.id}")
                                        hxTarget("body")
                                        hxPushUrl()
                                        hxIndicator("this > div.$HTMX_INDICATOR")
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
                                            if (directors.containsKey(movie.id.toLong())) {
                                                +"Directed by "
                                                +directors.getValue(movie.id.toLong())
                                                    .joinToString { person -> person.name }
                                            } else {
                                                div {
                                                    hxGet("/html/movies/${movie.id}/people")
                                                    hxTrigger("load")
                                                    hxTarget("this")
                                                    hxSwap("outerHTML")
                                                    hxVals {
                                                        put("role", "Director")
                                                    }
                                                    div(HTMX_INDICATOR) {
                                                        +"Directed by..."
                                                    }
                                                }
                                            }
                                        }
                                        div(HTMX_INDICATOR) {
                                            +"Loading..."
                                        }
                                    }
                                }
                            }
                        }
                    }
                } ?: call.parameters["director"]?.let { director ->
                    val principal = call.userPrincipalOrNull()
                    val credits = tmdbClient.getPersonMovieCredits(director.toInt())
                    call.respondHtml {
                        page(config, principal) {
                            credits.crew.filter { it.job == "Director" }.forEach {
                                p {
                                    a {
                                        href = "/html/movies/${it.id}"
                                        hxBoost()
                                        +it.title
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
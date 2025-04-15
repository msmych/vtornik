package uk.matvey.vtornik.web.movie.search

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.div
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

fun Route.movieSearchRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    tagRepository: TagRepository,
    movieRepository: MovieRepository,
    personRepository: PersonRepository,
) {
    authJwtOptional {
        route("/search") {
            get {
                if (call.parameters.contains("q")) {
                    val q = call.parameters.getOrFail("q")
                    val movies = tmdbClient.searchMovies(q)
                    val directors = personRepository.findAllPeopleByMoviesIds(
                        movies.results.map { it.id },
                        Movie.Role.DIRECTOR
                    )
                    call.respondHtml {
                        body {
                            div("col gap-8") {
                                movies.results.forEach { movie ->
                                    movieSearchResultItemHtml(
                                        MovieSearchResultItem(
                                            id = movie.id,
                                            title = movie.title,
                                            originalTitle = movie.originalTitle(),
                                            posterUrl = movie.posterUrl(),
                                            releaseDate = movie.releaseDate(),
                                        ),
                                        directors[movie.id],
                                    )
                                }
                            }
                        }
                    }
                } else if (call.parameters.contains("director")) {
                    val principal = call.userPrincipalOrNull()
                    val director = call.parameters.getOrFail("director").toLong()
                    val credits = tmdbClient.getPersonMovieCredits(director)
                    val directors = personRepository.findAllPeopleByMoviesIds(
                        credits.crew.filter { it.job == "Director" }.map { it.id },
                        Movie.Role.DIRECTOR
                    )
                    call.respondHtml {
                        page(config, principal) {
                            div("col gap-8") {
                                credits.crew.filter { it.job == "Director" }
                                    .sortedByDescending { it.releaseDate() }
                                    .forEach { item ->
                                        movieSearchResultItemHtml(
                                            MovieSearchResultItem(
                                                id = item.id,
                                                title = item.title,
                                                originalTitle = item.originalTitle(),
                                                posterUrl = item.posterUrl(),
                                                releaseDate = item.releaseDate(),
                                            ),
                                            directors = directors[item.id],
                                        )
                                    }
                            }
                        }
                    }
                } else {
                    val principal = call.userPrincipal()
                    val tag = call.parameters.getOrFail("tag")
                    val tags = tagRepository.findAllByUserAndTag(principal.id, tag)
                    val movies = movieRepository.findAllByIds(tags.map { it.movieId })
                    val directors = personRepository.findAllPeopleByMoviesIds(
                        movies.map { it.id },
                        Movie.Role.DIRECTOR
                    )
                    call.respondHtml {
                        page(config, principal) {
                            div("col gap-8") {
                                movies.forEach { movie ->
                                    movieSearchResultItemHtml(
                                        MovieSearchResultItem(
                                            id = movie.id,
                                            title = movie.title,
                                            originalTitle = movie.details.tmdb?.originalTitle(),
                                            posterUrl = movie.details.tmdb?.posterUrl(),
                                            releaseDate = movie.details.tmdb?.releaseDateOrNull(),
                                        ),
                                        directors = directors[movie.id],
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

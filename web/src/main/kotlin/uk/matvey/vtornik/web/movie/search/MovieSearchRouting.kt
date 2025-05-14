package uk.matvey.vtornik.web.movie.search

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h3
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.TmdbImages
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.PersonRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipalOrNull
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.MovieCard
import uk.matvey.vtornik.web.movie.movieCardHtml
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS
import uk.matvey.vtornik.web.page

fun Route.movieSearchRouting(
    config: WebConfig,
    tmdbClient: TmdbClient,
    tmdbImages: TmdbImages,
    tagRepository: TagRepository,
    movieRepository: MovieRepository,
    personRepository: PersonRepository,
) {
    authJwtOptional {
        route("/search") {
            get {
                if (call.parameters.contains("q")) {
                    val q = call.parameters.getOrFail("q")
                    val movies = tmdbClient.searchMovies(q.lowercase())
                    val directors = personRepository.findAllPeopleByMoviesIds(
                        movies.results.map { it.id },
                        Movie.Role.DIRECTOR
                    )
                    call.respondHtml {
                        body {
                            div("col gap-8 pane") {
                                movies.results.forEach { movie ->
                                    movieSearchResultItemHtml(
                                        movie = MovieSearchResultItem(
                                            id = movie.id,
                                            title = movie.title,
                                            originalTitle = movie.originalTitle(),
                                            posterUrl = movie.posterPath?.let {
                                                tmdbImages.posterUrl(it, "w92")
                                            },
                                            releaseDate = movie.releaseDate(),
                                        ),
                                        config = config,
                                        directors = directors[movie.id],
                                    )
                                }
                            }
                        }
                    }
                } else if (call.parameters.contains("director")) {
                    val principal = call.userPrincipalOrNull()
                    val directorId = call.parameters.getOrFail("director").toLong()
                    val directorDetails = tmdbClient.getPersonDetails(directorId)
                    val credits = tmdbClient.getPersonMovieCredits(directorId)
                    call.respondHtml {
                        page(config, principal, directorDetails.name, "vtornik") {
                            h3 {
                                +"Directed by ${directorDetails.name}"
                            }
                            div("row gap-8 wrap") {
                                credits.crew.filter { it.job == "Director" }
                                    .sortedByDescending { it.releaseDate() }
                                    .forEach { item ->
                                        movieCardHtml(
                                            movie = MovieCard(
                                                id = item.id,
                                                title = item.title,
                                                posterPath = item.posterPath,
                                            ),
                                            tmdbImages = tmdbImages,
                                            config = config,
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
                    call.respondHtml {
                        val tagLabel = STANDARD_TAGS.find { it.tag == tag }?.label ?: tag
                        page(config, principal, tagLabel, "vtornik") {
                            h3 {
                                +tagLabel
                            }
                            div("row gap-8 wrap") {
                                movies.forEach { movie ->
                                    movieCardHtml(
                                        movie = MovieCard(
                                            id = movie.id,
                                            title = movie.title,
                                            posterPath = movie.details.tmdb?.posterPath,
                                        ),
                                        tmdbImages = tmdbImages,
                                        config = config,
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

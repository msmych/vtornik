package uk.matvey.vtornik.web.movie.search

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.serialization.json.JsonPrimitive
import uk.matvey.slon.ktor.Resource
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.TmdbImages
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.PersonRepository
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.web.auth.Auth.Companion.authJwtOptional
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipal
import uk.matvey.vtornik.web.auth.UserPrincipal.Companion.userPrincipalOrNull
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.MovieService.MovieDetails
import uk.matvey.vtornik.web.movie.movieCardHtml
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS
import uk.matvey.vtornik.web.page.page

class MovieSearchResource(
    private val config: WebConfig,
    private val tmdbClient: TmdbClient,
    private val tmdbImages: TmdbImages,
    private val tagRepository: TagRepository,
    private val movieRepository: MovieRepository,
    private val personRepository: PersonRepository,
) : Resource {

    override fun Route.routing() {
        authJwtOptional {
            route("/search") {
                get {
                    if (call.parameters.contains("q")) {
                        searchByQuery()
                    } else if (call.parameters.contains("director")) {
                        searchByDirector()
                    } else if (call.parameters.contains("tag")) {
                        searchByTag()
                    }
                }
            }
        }
    }

    private suspend fun RoutingContext.searchByQuery() {
        val q = call.parameters.getOrFail("q")
        val movies = tmdbClient.searchMovies(q.lowercase())
        val directors = personRepository.findAllPeopleByMoviesIds(
            movies.results.map { it.id },
            Movie.Role.DIRECTOR
        )
        call.respondHtml {
            body {
                div("col gap-8 pane") {
                    if (movies.results.isEmpty()) {
                        +"Nothing found for '$q'"
                    } else {
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
        }
    }

    private suspend fun RoutingContext.searchByDirector() {
        val principal = call.userPrincipalOrNull()
        val directorId = call.parameters.getOrFail("director").toLong()
        val directorDetails = tmdbClient.getPersonDetails(directorId)
        val credits = tmdbClient.getPersonMovieCredits(directorId)
        call.respondHtml {
            page(config, principal, directorDetails.name, call.request, "vtornik") {
                h3 {
                    +"Directed by ${directorDetails.name}"
                }
                div("row gap-8 wrap") {
                    credits.crew.filter { it.job == "Director" }
                        .sortedByDescending { it.releaseDate() }
                        .forEach { item ->
                            movieCardHtml(
                                movie = MovieDetails(
                                    id = item.id,
                                    title = item.title,
                                    posterUrl = item.posterPath?.let { tmdbImages.posterUrl(it, "w500") },
                                ),
                                config = config,
                            )
                        }
                }
            }
        }
    }

    private suspend fun RoutingContext.searchByTag() {
        val principal = call.userPrincipal()
        val type = Tag.Type.valueOf(call.parameters.getOrFail("tag"))
        val tags = if (type == Tag.Type.NOTE) {
            tagRepository.findAllNotesByUserId(principal.id)
        } else {
            tagRepository.findAllByUserIdTypeAndValue(principal.id, type, JsonPrimitive(true))
        }
        val movies = movieRepository.findAllByIds(tags.map { it.movieId })
        call.respondHtml {
            val tagLabel = if (type == Tag.Type.NOTE) {
                "Movies with notes"
            } else {
                STANDARD_TAGS.find { it.tag == type.name }?.label ?: type.name
            }
            page(config, principal, tagLabel, call.request, "vtornik") {
                h3 {
                    +tagLabel
                }
                div("row gap-8 wrap") {
                    movies.forEach { movie ->
                        movieCardHtml(
                            movie = MovieDetails(
                                id = movie.id,
                                title = movie.title,
                                posterUrl = movie.tmdb?.posterPath?.let { tmdbImages.posterUrl(it, "w500") },
                            ),
                            config = config,
                        )
                    }
                }
            }
        }
    }
}

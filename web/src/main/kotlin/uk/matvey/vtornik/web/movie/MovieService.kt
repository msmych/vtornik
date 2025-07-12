package uk.matvey.vtornik.web.movie

import kotlinx.serialization.Serializable
import uk.matvey.tmdb.TmdbClient
import uk.matvey.tmdb.TmdbImages
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.MoviePersonRepository
import uk.matvey.vtornik.person.PersonRepository

class MovieService(
    private val movieRepository: MovieRepository,
    private val moviePersonRepository: MoviePersonRepository,
    private val personRepository: PersonRepository,
    private val tmdbClient: TmdbClient,
    private val tmdbImages: TmdbImages,
) {

    suspend fun ensureMovie(tmdbMovieId: Long): Movie {
        return movieRepository.findById(tmdbMovieId) ?: run {
            val movieDetails = tmdbClient.getMovieDetails(
                movieId = tmdbMovieId,
                appendToResponse = listOf("credits")
            )
            movieRepository.add(
                id = tmdbMovieId,
                title = movieDetails.title,
                overview = movieDetails.overview,
                runtime = movieDetails.runtime,
                releaseDate = movieDetails.releaseDate(),
                originalTitle = movieDetails.originalTitle,
                tmdb = Movie.Tmdb(
                    posterPath = movieDetails.posterPath,
                    backdropPath = movieDetails.backdropPath,
                )
            )
            val (_, crew) = movieDetails.extraCredits()
            crew.filter { it.job == "Director" }.forEach {
                val personId = personRepository.ensurePerson(
                    id = it.id,
                    name = it.name,
                )
                moviePersonRepository.addMoviePerson(tmdbMovieId, personId, Movie.Role.DIRECTOR)
            }
            movieRepository.getById(tmdbMovieId)
        }
    }

    @Serializable
    data class MovieDetails(
        val id: Long,
        val title: String,
        val posterUrl: String?,
    )

    suspend fun getNowPlaying(): List<MovieDetails> {
        return tmdbClient.nowPlayingMovies().results
            .map {
                MovieDetails(
                    id = it.id,
                    title = it.title,
                    posterUrl = it.posterPath?.let { path -> tmdbImages.posterUrl(path, "w500") }
                )
            }
    }

    suspend fun getUpcoming(): List<MovieDetails> {
        return tmdbClient.upcomingMovies().results
            .map {
                MovieDetails(
                    id = it.id,
                    title = it.title,
                    posterUrl = it.posterPath?.let { path -> tmdbImages.posterUrl(path, "w500") }
                )
            }
    }
}
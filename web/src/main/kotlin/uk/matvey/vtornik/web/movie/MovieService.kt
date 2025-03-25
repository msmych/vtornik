package uk.matvey.vtornik.web.movie

import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.Movie
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.MoviePersonRepository
import uk.matvey.vtornik.person.Person
import uk.matvey.vtornik.person.PersonRepository

class MovieService(
    private val movieRepository: MovieRepository,
    private val moviePersonRepository: MoviePersonRepository,
    private val personRepository: PersonRepository,
    private val tmdbClient: TmdbClient,
) {

    suspend fun ensureMovie(tmdbMovieId: Long): Movie {
        return movieRepository.findById(tmdbMovieId) ?: run {
            val movieDetails = tmdbClient.getMovieDetails(
                movieId = tmdbMovieId,
                appendToResponse = listOf("credits")
            )
            movieRepository.add(
                Movie.Details.Tmdb(
                    id = tmdbMovieId,
                    title = movieDetails.title,
                    overview = movieDetails.overview,
                    releaseDate = movieDetails.releaseDate()?.toString(),
                )
            )
            val (_, crew) = movieDetails.extraCredits()
            crew.filter { it.job == "Director" }.forEach {
                val personId = personRepository.add(
                    Person.Details.Tmdb(
                        id = it.id,
                        name = it.name,
                    ),
                )
                moviePersonRepository.addMoviePerson(tmdbMovieId, personId, Movie.Role.DIRECTOR)
            }
            movieRepository.getById(tmdbMovieId)
        }
    }
}
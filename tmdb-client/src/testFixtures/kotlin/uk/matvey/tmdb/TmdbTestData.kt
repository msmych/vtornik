package uk.matvey.tmdb

import uk.matvey.slon.random.randomParagraph
import uk.matvey.slon.random.randomSentence
import uk.matvey.slon.random.randomWord
import java.time.LocalDate
import kotlin.random.Random

fun aSearchMovieResponse(results: List<TmdbClient.SearchMovieResponse.ResultItem>) = TmdbClient.SearchMovieResponse(
    page = 1,
    results = results,
    totalPages = 1,
    totalResults = results.size,
)

fun aSearchMovieResponseResultItem(
    id: Long = Random.nextLong(),
    title: String = randomWord(),
    releaseDate: String = LocalDate.now().toString(),
) = TmdbClient.SearchMovieResponse.ResultItem(
    id = id,
    title = title,
    releaseDate = releaseDate,
    posterPath = null,
    backdropPath = null,
    originalTitle = null,
)

fun aMovieDetailsResponse(
    id: Long = Random.nextLong(),
    overview: String = randomParagraph(),
    title: String = randomSentence(),
    releaseDate: String = LocalDate.now().toString(),
) = TmdbClient.MovieDetailsResponse(
    id = id,
    overview = overview,
    title = title,
    releaseDate = releaseDate,
    posterPath = null,
    backdropPath = null,
    originalTitle = null,
)
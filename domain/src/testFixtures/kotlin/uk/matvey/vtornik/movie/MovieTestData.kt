package uk.matvey.vtornik.movie

import uk.matvey.slon.random.randomParagraph
import uk.matvey.slon.random.randomWord
import java.time.LocalDate
import kotlin.random.Random

fun aMovieTmdbDetails(
    id: Long = Random.nextLong(),
    title: String = randomWord(),
    runtime: Int = Random.nextInt(70, 300),
    overview: String = randomParagraph(),
    releaseDate: String? = LocalDate.now().toString(),
) = Movie.Details.Tmdb(
    id = id,
    title = title,
    runtime = runtime,
    overview = overview,
    releaseDate = releaseDate,
    posterPath = null,
    backdropPath = null,
    originalTitle = null,
)

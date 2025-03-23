package uk.matvey.vtornik.movie

import uk.matvey.slon.random.randomParagraph
import uk.matvey.slon.random.randomWord
import java.time.LocalDate
import kotlin.random.Random

object MovieTestData {

    fun aTmdbDetails(
        id: Long = Random.nextLong(),
        title: String = randomWord(),
        overview: String = randomParagraph(),
        releaseDate: String? = LocalDate.now().toString(),
    ) = Movie.Details.Tmdb(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
    )
}
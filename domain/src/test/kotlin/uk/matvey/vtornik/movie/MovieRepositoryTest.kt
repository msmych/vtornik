package uk.matvey.vtornik.movie

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.slon.random.randomWord
import uk.matvey.vtornik.DomainTestSetup
import java.time.LocalDate
import kotlin.random.Random

class MovieRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add movie`() = runTest {
        // given
        val repository = MovieRepository(db)
        val title = randomWord()
        val releaseDate = LocalDate.now()
        val tmdb = Movie.Tmdb(
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg"
        )

        // when
        val id = repository.add(
            id = Random.nextLong(),
            title = title,
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = releaseDate,
            tmdb = tmdb
        )

        // then
        val movie = repository.findById(id)
        assertThat(movie).isNotNull
        assertThat(movie?.id).isEqualTo(id)
        assertThat(movie?.title).isEqualTo(title)
        assertThat(movie?.releaseDate).isEqualTo(releaseDate)
        assertThat(movie?.tmdb).isEqualTo(tmdb)
    }

    @Test
    fun `should find all movies by ids`() = runTest {
        // given
        val repository = MovieRepository(db)
        val id1 = repository.add(
            id = Random.nextLong(),
            title = randomWord(),
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = LocalDate.now(),
            tmdb = null,
        )
        val id2 = repository.add(
            id = Random.nextLong(),
            title = randomWord(),
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = LocalDate.now(),
            tmdb = null,
        )

        // when
        val movies = repository.findAllByIds(listOf(id1, id2))

        // then
        assertThat(movies).hasSize(2)
    }
}
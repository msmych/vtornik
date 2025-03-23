package uk.matvey.vtornik.movie

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup
import uk.matvey.vtornik.movie.MovieTestData.aTmdbDetails

class MovieRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add movie`() = runTest {
        // given
        val repository = MovieRepository(db)
        val tmdbDetails = aTmdbDetails()

        // when
        val id = repository.add(tmdbDetails)

        // then
        val movie = repository.findById(id)
        assertThat(movie).isNotNull
        assertThat(movie?.id).isEqualTo(id)
        assertThat(movie?.title).isEqualTo(tmdbDetails.title)
        assertThat(movie?.year).isEqualTo(tmdbDetails.releaseDateOrNull()?.year)
        assertThat(movie?.details?.tmdb).isEqualTo(tmdbDetails)
    }

    @Test
    fun `should find all movies by ids`() = runTest {
        // given
        val repository = MovieRepository(db)
        val id1 = repository.add(aTmdbDetails())
        val id2 = repository.add(aTmdbDetails())

        // when
        val movies = repository.findAllByIds(listOf(id1, id2))

        // then
        assertThat(movies).hasSize(2)
    }
}
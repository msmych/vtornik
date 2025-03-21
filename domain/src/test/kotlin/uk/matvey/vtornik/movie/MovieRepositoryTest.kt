package uk.matvey.vtornik.movie

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup

class MovieRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add movie`() = runTest {
        // given
        val repository = MovieRepository(db)

        // when
        val id = repository.add(
            "Title", Movie.Details(
                tmdb = Movie.Details.Tmdb(
                    id = 1234,
                    overview = "Overview",
                    releaseDate = "2025-03-19",
                )
            )
        )

        // then
        val movie = repository.findById(id)
        assertThat(movie).isNotNull
        assertThat(movie?.id).isEqualTo(id)
        assertThat(movie?.title).isEqualTo("Title")
        assertThat(movie?.year).isEqualTo(2025)
        assertThat(movie?.details?.tmdb?.id).isEqualTo(1234)
        assertThat(movie?.details?.tmdb?.overview).isEqualTo("Overview")
        assertThat(movie?.details?.tmdb?.releaseDate).isEqualTo("2025-03-19")
    }
}
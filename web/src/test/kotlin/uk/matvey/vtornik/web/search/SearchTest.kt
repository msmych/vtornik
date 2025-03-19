package uk.matvey.vtornik.web.search

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.TestServerModule
import uk.matvey.vtornik.web.TestServerModule.testServerModule

class SearchTest {

    @Test
    fun `should return movies search result`() = testApplication {
        // given
        application {
            testServerModule()
        }
        val movie1 = TmdbClient.SearchMovieResponse.ResultItem(
            id = 1,
            title = "Movie 1",
            releaseDate = "2025-03-19"
        )
        val movie2 = TmdbClient.SearchMovieResponse.ResultItem(
            id = 2,
            title = "Movie 2",
            releaseDate = "",
        )
        coEvery {
            TestServerModule.tmdbClient.searchMovies("movie")
        } returns TmdbClient.SearchMovieResponse(
            page = 1,
            results = listOf(
                movie1,
                movie2,
            ),
            totalPages = 1,
            totalResults = 2,
        )

        // when
        val rs = client.get("/html/search?q=movie")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(movie1.title)
            .contains(movie2.title)
    }
}
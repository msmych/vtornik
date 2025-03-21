package uk.matvey.vtornik.web.movie

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.WebTestSetup

class MovieTest : WebTestSetup() {

    @Test
    fun `should return movie details by id`() = testApplication {
        // given
        application {
            testServerModule()
        }
        coEvery {
            tmdbClient.getMovieDetails(1234)
        } returns TmdbClient.MovieDetailsResponse(
            id = 1234,
            title = "Title",
            overview = "Overview",
        )

        // when
        val rs = client.get("/html/movies/1234")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Title")
            .contains("Overview")
    }
}
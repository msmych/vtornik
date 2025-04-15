package uk.matvey.vtornik.web.movie.person

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.tmdb.aMovieCreditsResponse
import uk.matvey.vtornik.web.WebTestSetup
import kotlin.random.Random

class PersonRoutingTest : WebTestSetup() {

    @Test
    fun `should return movie directors`() = testApplication {
        // given
        application {
            testServerModule()
        }

        val movieId = Random.nextLong()

        coEvery {
            services.tmdbClient.getMovieCredits(movieId)
        } returns aMovieCreditsResponse()

        // when
        val rs = client.get("/html/movies/$movieId/people?role=Director") {
            appendJwtCookie()
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Directed by")
    }
}
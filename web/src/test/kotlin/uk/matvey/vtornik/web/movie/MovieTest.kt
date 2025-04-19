package uk.matvey.vtornik.web.movie

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.matvey.tmdb.aMovieDetailsResponse
import uk.matvey.vtornik.web.WebTestSetup

class MovieTest : WebTestSetup() {

    private val movieDetails = aMovieDetailsResponse().apply {
        extras = buildJsonObject {
            putJsonObject("credits") {
                put("cast", JsonArray(listOf()))
                put("crew", JsonArray(listOf()))
            }
        }
    }

    @BeforeEach
    fun setup() {
        coEvery {
            services.tmdbClient.getMovieDetails(1234, listOf("credits"))
        } returns movieDetails
    }

    @Test
    fun `should return movie details by id`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/html/movies/1234")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("<head>")
            .contains(movieDetails.title)
            .contains(movieDetails.overview)
    }

    @Test
    fun `should return movie details with tags for authenticated user`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/html/movies/1234") {
            appendJwtCookie()
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Watch list")
            .contains("Watched")
    }
}
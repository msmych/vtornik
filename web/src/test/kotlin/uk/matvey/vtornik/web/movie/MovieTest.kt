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
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.WebTestSetup

class MovieTest : WebTestSetup() {

    @BeforeEach
    fun setup() {
        coEvery {
            services.tmdbClient.getMovieDetails(1234, listOf("credits"))
        } returns TmdbClient.MovieDetailsResponse(
            id = 1234,
            title = "Title",
            overview = "Overview",
            releaseDate = "2025-03-19",
            posterPath = null,
            backdropPath = null,
            originalTitle = null,
        ).apply {
            extras = buildJsonObject {
                putJsonObject("credits") {
                    put("cast", JsonArray(listOf()))
                    put("crew", JsonArray(listOf()))
                }
            }
        }
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
            .contains("Title")
            .contains("Overview")
            .contains("2025")
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
            .contains("Add to")
    }
}
package uk.matvey.vtornik.web.movie

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.matvey.tmdb.aMovieDetailsResponse
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.web.WebTestSetup
import kotlin.random.Random

class MovieHtmlResourceTest : WebTestSetup() {

    private val movieDetails = aMovieDetailsResponse().apply {
        extras = buildJsonObject {
            putJsonObject("credits") {
                put("cast", JsonArray(listOf()))
                put("crew", JsonArray(listOf()))
            }
        }
    }

    private val movieId = Random.nextLong()

    @BeforeEach
    fun setup() {
        coEvery {
            services.tmdbClient.getMovieDetails(movieId, listOf("credits"))
        } returns movieDetails
    }

    @Test
    fun `should return movie details by id`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/html/movies/$movieId")

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
        val userId = Random.nextInt()
        services.tagRepository.set(userId, movieId, Tag.Type.WATCHLIST, JsonPrimitive(false))

        // when
        val rs = client.get("/html/movies/$movieId") {
            appendJwtCookie(userId)
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Watchlist")
            .contains("Watched")
            .contains("Like")
            .contains("""<label hx-put="/html/movies/$movieId/tags/WATCHLIST" hx-swap="outerHTML" hx-vals="{&quot;value&quot;:true}">""")
    }
}
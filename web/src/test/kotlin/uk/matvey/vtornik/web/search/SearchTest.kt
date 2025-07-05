package uk.matvey.vtornik.web.search

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.slon.random.randomWord
import uk.matvey.tmdb.aSearchMovieResponse
import uk.matvey.tmdb.aSearchMovieResponseResultItem
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.web.WebTestSetup
import kotlin.random.Random

class SearchTest : WebTestSetup() {

    @Test
    fun `should search movies by query`() = testApplication {
        // given
        application {
            testServerModule()
        }
        val movie1 = aSearchMovieResponseResultItem()
        val movie2 = aSearchMovieResponseResultItem()
        coEvery {
            services.tmdbClient.searchMovies("movie")
        } returns aSearchMovieResponse(listOf(movie1, movie2))

        // when
        val rs = client.get("/html/movies/search?q=movie")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(movie1.title)
            .contains(movie2.title)
    }

    @Test
    fun `should search movie by tag`() = testApplication {
        // given
        application {
            testServerModule()
        }
        val userId = Random.nextInt()
        val title1 = randomWord()
        val movieId1 = services.movieRepository.add(
            id = Random.nextLong(),
            title = title1,
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = null,
            tmdb = null
        )
        val title2 = randomWord()
        val movieId2 = services.movieRepository.add(
            id = Random.nextLong(),
            title = title2,
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = null,
            tmdb = null
        )
        val movieId3 = services.movieRepository.add(
            id = Random.nextLong(),
            title = randomWord(),
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = null,
            tmdb = null
        )
        val movieId4 = services.movieRepository.add(
            id = Random.nextLong(),
            title = randomWord(),
            runtime = Random.nextInt(60, 180),
            overview = randomWord(),
            originalTitle = randomWord(),
            releaseDate = null,
            tmdb = null
        )
        services.tagRepository.set(userId, movieId1, Tag.Type.WATCHED, JsonPrimitive(true))
        services.tagRepository.set(userId, movieId2, Tag.Type.WATCHED, JsonPrimitive(true))
        services.tagRepository.set(userId, movieId3, Tag.Type.WATCHLIST, JsonPrimitive(true))

        // when
        val rs = client.get("/html/movies/search?tag=WATCHED") {
            appendJwtCookie(userId)
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(title1)
            .contains(title2)
    }
}
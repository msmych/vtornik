package uk.matvey.vtornik.web.search

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.slon.random.randomWord
import uk.matvey.tmdb.aSearchMovieResponse
import uk.matvey.tmdb.aSearchMovieResponseResultItem
import uk.matvey.vtornik.movie.aMovieTmdbDetails
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
        val tag1 = randomWord()
        val tag2 = randomWord()
        val tmdbDetails1 = aMovieTmdbDetails()
        val movieId1 = services.movieRepository.add(tmdbDetails1)
        val tmdbDetails2 = aMovieTmdbDetails()
        val movieId2 = services.movieRepository.add(tmdbDetails2)
        val tmdbDetails3 = aMovieTmdbDetails()
        val movieId3 = services.movieRepository.add(tmdbDetails3)
        val tmdbDetails4 = aMovieTmdbDetails()
        val movieId4 = services.movieRepository.add(tmdbDetails4)
        services.tagRepository.add(userId, movieId1, tag1)
        services.tagRepository.add(userId, movieId2, tag1)
        services.tagRepository.add(userId, movieId3, tag2)

        // when
        val rs = client.get("/html/movies/search?tag=$tag1") {
            appendJwtCookie(userId)
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(tmdbDetails1.title)
            .contains(tmdbDetails2.title)
    }
}
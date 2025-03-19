package uk.matvey.tmdb

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class TmdbClient {

    private val httpClient = HttpClient(engineFactory = CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    @Serializable
    data class SearchMovieResponse(
        val page: Int,
        val results: List<ResultItem>,
    ) {

        @Serializable
        data class ResultItem(
            val id: Int,
            val title: String,
        )
    }

    suspend fun searchMovies(query: String): SearchMovieResponse {
        return httpClient.get("https://api.themoviedb.org/3/search/movie") {
            bearerAuth(System.getenv("TMDB_API_KEY"))
            parameter("query", query)
        }.body()
    }
}
package uk.matvey.tmdb

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate

class TmdbClient(engine: HttpClientEngine) {

    private val httpClient = HttpClient(engine) {
        defaultRequest {
            bearerAuth(System.getenv("TMDB_API_KEY"))
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    @Serializable
    data class MovieDetailsResponse(
        val id: Long,
        val overview: String,
        val title: String,
        @SerialName("release_date") val releaseDate: String,
    ) {
        fun releaseDate() = releaseDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
    }

    suspend fun getMovieDetails(movieId: Long): MovieDetailsResponse {
        return httpClient.get("https://api.themoviedb.org/3/movie/$movieId").body()
    }

    @Serializable
    data class SearchMovieResponse(
        val page: Int,
        val results: List<ResultItem>,
        @SerialName("total_pages") val totalPages: Int,
        @SerialName("total_results") val totalResults: Int,
    ) {

        @Serializable
        data class ResultItem(
            val id: Int,
            val title: String,
            @SerialName("release_date") val releaseDate: String,
        ) {
            fun releaseDate() = releaseDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        }
    }

    suspend fun searchMovies(query: String): SearchMovieResponse {
        return httpClient.get("https://api.themoviedb.org/3/search/movie") {
            parameter("query", query)
        }.body()
    }
}
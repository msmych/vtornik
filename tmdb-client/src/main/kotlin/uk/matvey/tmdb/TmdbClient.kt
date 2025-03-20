package uk.matvey.tmdb

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

class TmdbClient {

    private val httpClient = HttpClient(engineFactory = CIO) {
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
            bearerAuth(System.getenv("TMDB_API_KEY"))
            parameter("query", query)
        }.body()
    }
}
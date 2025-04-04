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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uk.matvey.tmdb.TmdbClient.MovieCredits.CastItem
import uk.matvey.tmdb.TmdbClient.MovieCredits.CrewItem
import java.time.LocalDate

class TmdbClient(engine: HttpClientEngine) {

    companion object {
        private val JSON = Json {
            ignoreUnknownKeys = true
        }
    }

    private val httpClient = HttpClient(engine) {
        defaultRequest {
            bearerAuth(System.getenv("TMDB_API_KEY"))
        }
        install(ContentNegotiation) {
            json(JSON)
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
        var extras = JsonObject(emptyMap())

        fun releaseDate() = releaseDate.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }

        fun extraCredits() = extras.getValue("credits").jsonObject.let { it: JsonObject ->
            val castItems = JSON.decodeFromJsonElement<List<CastItem>>(it.getValue("cast"))
            val crewItems = JSON.decodeFromJsonElement<List<CrewItem>>(it.getValue("crew"))
            castItems to crewItems
        }
    }

    suspend fun getMovieDetails(
        movieId: Long,
        appendToResponse: List<String> = emptyList()
    ): MovieDetailsResponse {
        val result: JsonObject = httpClient.get("https://api.themoviedb.org/3/movie/$movieId") {
            appendToResponse.takeIf { it.isNotEmpty() }
                ?.let { parameter("append_to_response", it.joinToString(",")) }
        }.body()
        val response = JSON.decodeFromJsonElement<MovieDetailsResponse>(result)
        response.extras = buildJsonObject {
            appendToResponse.forEach { extra ->
                put(extra, result.getValue(extra))
            }
        }
        return response
    }

    @Serializable
    data class MovieCredits(
        val cast: List<CastItem>,
        val crew: List<CrewItem>,
    ) {

        @Serializable
        data class CastItem(
            val id: Long,
        )

        @Serializable
        data class CrewItem(
            val id: Long,
            val name: String,
            val job: String,
        )
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
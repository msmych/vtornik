package uk.matvey.vtornik.movie

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

data class Movie(
    val id: Long,
    val title: String,
    val year: Int?,
    val details: Details,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Details(
        val tmdb: Tmdb? = null,
    ) {

        @Serializable
        data class Tmdb(
            val id: Long,
            val overview: String,
            val releaseDate: String? = null,
        ) {
            fun releaseDate() = releaseDate?.let { LocalDate.parse(it) }
        }
    }
}

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

        fun tmdb() = requireNotNull(tmdb) {
            "TMDb details were missing"
        }

        @Serializable
        data class Tmdb(
            val id: Long,
            val title: String,
            val overview: String,
            val releaseDate: String? = null,
        ) {
            fun releaseDateOrNull() = releaseDate?.let { LocalDate.parse(it) }
        }
    }
}

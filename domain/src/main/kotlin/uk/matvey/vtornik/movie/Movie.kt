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
            val posterPath: String? = null,
            val backdropPath: String? = null,
            val originalTitle: String? = null,
        ) {
            fun originalTitle() = originalTitle.takeUnless { it == title }

            fun releaseDateOrNull() = releaseDate?.let { LocalDate.parse(it) }

            fun backdropUrl() = backdropPath?.let {
                "https://media.themoviedb.org/t/p/w1920_and_h800_multi_faces$it"
            }
        }
    }

    enum class Role {
        DIRECTOR,
        ACTOR,
    }
}

package uk.matvey.vtornik.movie

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

data class Movie(
    val id: Long,
    val title: String,
    val runtime: Int,
    val overview: String,
    val releaseDate: LocalDate?,
    val originalTitle: String?,
    val tmdb: Tmdb? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Tmdb(
        val posterPath: String? = null,
        val backdropPath: String? = null,
    )

    enum class Role {
        DIRECTOR,
        ACTOR,
    }

    fun addMention(link: String, name: String): Movie {
        return copy()
    }
}

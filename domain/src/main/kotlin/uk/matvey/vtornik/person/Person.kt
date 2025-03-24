package uk.matvey.vtornik.person

import kotlinx.serialization.Serializable
import java.time.Instant

data class Person(
    val id: Long,
    val name: String,
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
            val name: String,
        )
    }
}

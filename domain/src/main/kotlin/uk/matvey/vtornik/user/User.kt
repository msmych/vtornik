package uk.matvey.vtornik.user

import kotlinx.serialization.Serializable
import java.time.Instant

data class User(
    val id: Int,
    val username: String,
    val details: Details,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Details(
        val github: Github? = null,
    ) {

        @Serializable
        data class Github(
            val id: Long,
            val login: String,
            val name: String,
        )
    }
}

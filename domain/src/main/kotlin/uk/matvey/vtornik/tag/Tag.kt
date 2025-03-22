package uk.matvey.vtornik.tag

import java.time.Instant

data class Tag(
    val userId: Int,
    val movieId: Long,
    val tag: String,
    val createdAt: Instant,
) {
}
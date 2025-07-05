package uk.matvey.vtornik.tag

import kotlinx.serialization.json.JsonElement
import java.time.Instant

data class Tag(
    val userId: Int,
    val movieId: Long,
    val type: Type,
    val payload: JsonElement,
    val createdAt: Instant,
) {

    enum class Type {
        WATCHED,
        WATCHLIST,
        LIKE,
        NOTE,
    }
}
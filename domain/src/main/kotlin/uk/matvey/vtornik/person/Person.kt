package uk.matvey.vtornik.person

import java.time.Instant

data class Person(
    val id: Long,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

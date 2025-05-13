package uk.matvey.vtornik.note

data class Note(
    val movieId: Long,
    val userId: Int,
    val note: String,
) {
}
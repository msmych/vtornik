package uk.matvey.vtornik.web.movie.tag

class TagView(
    val tag: String,
    val label: String,
) {

    companion object {

        val WATCHLIST_TAG = TagView(
            tag = "watchlist",
            label = "Watch list",
        )

        val WATCHED_TAG = TagView(
            tag = "watched",
            label = "Watched",
        )

        val STAR_TAG = TagView(
            tag = "star",
            label = "Star",
        )

        val STANDARD_TAGS = listOf(
            WATCHLIST_TAG,
            WATCHED_TAG,
            STAR_TAG,
        )
    }
}
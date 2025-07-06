package uk.matvey.vtornik.web.movie.tag

class TagView(
    val tag: String,
    val label: String,
) {

    companion object {

        val WATCHLIST_TAG = TagView(
            tag = "WATCHLIST",
            label = "Watchlist",
        )

        val WATCHED_TAG = TagView(
            tag = "WATCHED",
            label = "Watched",
        )

        val STAR_TAG = TagView(
            tag = "LIKE",
            label = "Like",
        )

        val STANDARD_TAGS = listOf(
            WATCHLIST_TAG,
            WATCHED_TAG,
            STAR_TAG,
        )
    }
}
package uk.matvey.vtornik.web.page

import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.serialization.json.put
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxVals

fun HtmlBlockTag.tagFilter(name: String, label: String) = a {
    href = "/html/movies/search"
    hxBoost()
    hxVals {
        put("tag", name)
    }
    +label
}

fun HtmlBlockTag.commentedFilter(label: String) = a {
    href = "/html/movies/search?commented"
    hxBoost()
    +label
}

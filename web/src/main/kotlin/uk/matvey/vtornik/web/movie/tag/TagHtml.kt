package uk.matvey.vtornik.web.movie.tag

import kotlinx.html.HtmlBlockTag
import kotlinx.html.checkBoxInput
import kotlinx.html.label
import uk.matvey.slon.html.hxDelete
import uk.matvey.slon.html.hxPost
import uk.matvey.slon.html.hxSwap

fun HtmlBlockTag.tagToggle(movieId: Long, tag: TagView, current: Boolean) = label {
    if (current) {
        hxDelete("/html/movies/$movieId/tags/${tag.tag}")
        hxSwap("outerHTML")
    } else {
        hxPost("/html/movies/$movieId/tags/${tag.tag}")
        hxSwap("outerHTML")
    }
    checkBoxInput {
        checked = current
    }
    +tag.label
}
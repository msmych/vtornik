package uk.matvey.vtornik.web.movie.tag

import io.ktor.htmx.HxAttributeKeys.Delete
import io.ktor.htmx.HxSwap.outerHtml
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.HtmlBlockTag
import kotlinx.html.checkBoxInput
import kotlinx.html.label

@OptIn(ExperimentalKtorApi::class)
fun HtmlBlockTag.tagToggle(movieId: Long, tag: TagView, current: Boolean) = label {
    if (current) {
        attributes.hx {
            swap = outerHtml
        }
        attributes[Delete] = "/html/movies/$movieId/tags/${tag.tag}"
    } else {
        attributes.hx {
            post = "/html/movies/$movieId/tags/${tag.tag}"
            swap = outerHtml
        }
    }
    checkBoxInput {
        checked = current
    }
    +tag.label
}
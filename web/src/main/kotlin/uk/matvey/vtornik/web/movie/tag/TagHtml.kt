package uk.matvey.vtornik.web.movie.tag

import io.ktor.htmx.HxAttributeKeys.Put
import io.ktor.htmx.HxSwap.outerHtml
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.HtmlBlockTag
import kotlinx.html.checkBoxInput
import kotlinx.html.label
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@OptIn(ExperimentalKtorApi::class)
fun HtmlBlockTag.tagToggle(movieId: Long, tag: TagView, current: Boolean) = label {
    attributes[Put] = "/html/movies/$movieId/tags/${tag.tag}"
    attributes.hx {
        swap = outerHtml
        vals = Json.encodeToString(buildJsonObject {
            put("value", JsonPrimitive(!current))
        })
    }
    checkBoxInput {
        checked = current
    }
    +tag.label
}
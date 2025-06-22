package uk.matvey.vtornik.web.page

import io.ktor.htmx.HxAttributeKeys.Boost
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalKtorApi::class)
fun HtmlBlockTag.tagFilter(name: String, label: String) = a {
    href = "/html/movies/search"
    attributes[Boost] = "true"
    attributes.hx {
        vals = Json.encodeToString(buildJsonObject {
            put("tag", name)
        })
    }
    +label
}

fun HtmlBlockTag.commentedFilter(label: String) = a {
    href = "/html/movies/search?commented"
    attributes[Boost] = "true"
    +label
}

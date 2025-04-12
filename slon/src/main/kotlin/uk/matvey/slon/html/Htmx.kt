package uk.matvey.slon.html

import kotlinx.html.HTMLTag
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

fun HTMLTag.hxBoost() {
    attributes["hx-boost"] = "true"
}

fun HTMLTag.hxGet(path: String) {
    attributes["hx-get"] = path
}

fun HTMLTag.hxPost(path: String) {
    attributes["hx-post"] = path
}

fun HTMLTag.hxPut(path: String) {
    attributes["hx-put"] = path
}

fun HTMLTag.hxPatch(path: String) {
    attributes["hx-patch"] = path
}

fun HTMLTag.hxDelete(path: String) {
    attributes["hx-delete"] = path
}

fun HTMLTag.hxTrigger(trigger: String) {
    attributes["hx-trigger"] = trigger
}

fun HTMLTag.hxTarget(target: String) {
    attributes["hx-target"] = target
}

fun HTMLTag.hxSwap(swap: String) {
    attributes["hx-swap"] = swap
}

fun HTMLTag.hxVals(block: JsonObjectBuilder.() -> Unit) {
    attributes["hx-vals"] = Json.encodeToString(buildJsonObject {
        block()
    })
}

fun HTMLTag.hxPushUrl(value: Boolean = true) {
    attributes["hx-push-url"] = value.toString()
}

fun HTMLTag.hxIndicator(value: String) {
    attributes["hx-indicator"] = value
}

const val HTMX_INDICATOR = "htmx-indicator"
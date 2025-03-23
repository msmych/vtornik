package uk.matvey.slon.html

import kotlinx.html.FlowContent
import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockInlineTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.visit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class SEARCH(
    val initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>
) : HTMLTag(
    "search",
    consumer,
    initialAttributes,
    null,
    false,
    false
), HtmlBlockInlineTag {

    companion object {

        @HtmlTagMarker
        @OptIn(ExperimentalContracts::class)
        inline fun FlowContent.search(classes: String? = null, crossinline block: SEARCH.() -> Unit = {}) {
            contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
            SEARCH(attributesMapOf("class", classes), consumer).visit(block)
        }
    }
}

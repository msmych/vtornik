package uk.matvey.vtornik.web

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.HTMLTag
import kotlinx.html.body
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.main
import kotlinx.html.searchInput
import kotlinx.html.submitInput
import kotlinx.html.title
import kotlinx.html.visit

fun Routing.indexRouting() {
    get {
        call.respondHtml {
            head {
                title = "Vtornik"
            }
            body {
                header {
                    h1 {
                        +"Vtornik"
                    }
                }
                main {
                    h3 {
                        +"Search a movie"
                    }
                    HTMLTag(
                        tagName = "search",
                        consumer = consumer,
                        initialAttributes = emptyMap(),
                        namespace = null,
                        inlineTag = false,
                        emptyTag = false
                    ).visit {
                        this@main.form(action = "/html/search") {
                            searchInput {
                                name = "q"
                                placeholder = "Brutalist"
                            }
                            submitInput {
                                value = "Search"
                            }
                        }
                    }
                }
            }
        }
    }
}
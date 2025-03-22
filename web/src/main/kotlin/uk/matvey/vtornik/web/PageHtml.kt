package uk.matvey.vtornik.web

import kotlinx.html.HTML
import kotlinx.html.HTMLTag
import kotlinx.html.MAIN
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.searchInput
import kotlinx.html.submitInput
import kotlinx.html.title
import kotlinx.html.visit

fun HTML.page(
    principal: UserPrincipal?,
    githubClientId: String?,
    block: MAIN.() -> Unit,
) {
    head {
        title("Vtornik")
        script {
            src = "https://unpkg.com/htmx.org@2.0.4"
            integrity = "sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+"
            crossorigin = ScriptCrossorigin.anonymous
        }
    }
    body {
        header {
            a {
                href = "/"
                h1 {
                    +"Vtornik"
                }
            }
            if (principal != null) {
                p {
                    +"Logged in as ${principal.username}. "
                    a {
                        href = "/logout"
                        +"Logout"
                    }
                }
            } else if (githubClientId != null) {
                p {
                    a {
                        href = "https://github.com/login/oauth/authorize?client_id=$githubClientId"
                        +"Login with GitHub"
                    }
                }
            }
        }
        HTMLTag(
            tagName = "search",
            consumer = consumer,
            initialAttributes = emptyMap(),
            namespace = null,
            inlineTag = false,
            emptyTag = false
        ).visit {
            this@body.form {
                attributes["hx-get"] = "/html/search"
                attributes["hx-target"] = "#search-results"
                searchInput {
                    name = "q"
                    placeholder = "The Brutalist"
                }
                submitInput {
                    value = "Search"
                    div(classes = "htmx-indicator") {
                        +"Searching..."
                    }
                }
            }
        }
        div {
            id = "search-results"
        }
        main {
            block()
        }
    }
}
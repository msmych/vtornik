package uk.matvey.vtornik.web

import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.HTMLTag
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.main
import kotlinx.html.script
import kotlinx.html.searchInput
import kotlinx.html.submitInput
import kotlinx.html.title
import kotlinx.html.visit

fun Routing.indexRouting(githubClientId: String?) {
    authenticate("jwt-optional") {
        get {
            val principal = call.principal<UserPrincipal>()
            call.respondHtml {
                head {
                    title = "Vtornik"
                    script {
                        src = "https://unpkg.com/htmx.org@2.0.4"
                        integrity = "sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+"
                        crossorigin = ScriptCrossorigin.anonymous
                    }
                }
                body {
                    header {
                        h1 {
                            +"Vtornik"
                        }
                        if (principal != null) {
                            +"Logged in as ${principal.username}. "
                            a {
                                href = "/logout"
                                +"Logout"
                            }
                        } else if (githubClientId != null) {
                            a {
                                href = "https://github.com/login/oauth/authorize?client_id=$githubClientId"
                                +"Login with GitHub"
                            }
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
                            this@main.form {
                                attributes["hx-get"] = "/html/search"
                                attributes["hx-target"] = "#main"
                                searchInput {
                                    name = "q"
                                    placeholder = "Brutalist"
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
                            id = "main"
                        }
                    }
                }
            }
        }
    }
}
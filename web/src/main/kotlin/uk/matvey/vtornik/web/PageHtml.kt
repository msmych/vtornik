package uk.matvey.vtornik.web

import kotlinx.html.HTML
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
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.searchInput
import kotlinx.html.submitInput
import kotlinx.html.title
import kotlinx.serialization.json.put
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.SEARCH.Companion.search
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxTarget
import uk.matvey.slon.html.hxVals
import uk.matvey.vtornik.web.config.WebConfig

fun HTML.page(
    config: WebConfig,
    principal: UserPrincipal?,
    block: MAIN.() -> Unit,
) {
    head {
        title("Vtornik")
        link {
            rel = "stylesheet"
            href = "/assets/styles.css"
        }
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
            } else {
                p {
                    a {
                        href = if (config.profile == WebConfig.Profile.MOCK) {
                            "/mock/login"
                        } else {
                            "https://github.com/login/oauth/authorize?client_id=${config.githubClientId}"
                        }
                        +"Login with GitHub"
                    }
                }
            }
            search {
                form(classes = "row gap-8") {
                    hxGet("/html/movies/search")
                    hxTarget("#search-results")
                    attributes["hx-indicator"] = "header > div.$HTMX_INDICATOR"
                    searchInput {
                        name = "q"
                        placeholder = "The Brutalist"
                    }
                    submitInput {
                        value = "Search"
                    }
                }
            }
            principal?.let {
                setOf("watchlist", "watched").forEach {
                    a {
                        href = "/html/movies/search"
                        hxBoost()
                        hxVals {
                            put("tag", it)
                        }
                        +it
                    }
                    +" "
                }
            }
            div(classes = HTMX_INDICATOR) {
                +"Searching..."
            }
            div {
                id = "search-results"
            }
        }
        main {
            block()
        }
    }
}
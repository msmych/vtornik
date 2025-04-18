package uk.matvey.vtornik.web

import kotlinx.html.HTML
import kotlinx.html.MAIN
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.script
import kotlinx.html.searchInput
import kotlinx.html.section
import kotlinx.html.submitInput
import kotlinx.html.title
import kotlinx.serialization.json.put
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.SEARCH.Companion.search
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxIndicator
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
        script {
            src = "/assets/script.js"
        }
    }
    body("col gap-32") {
        header("col gap-16") {
            section("split gap-8") {
                a {
                    href = "/"
                    b {
                        +"Vtornik"
                    }
                }
                div("row gap-8") {
                    if (principal != null) {
                        div {
                            +"Logged in as "
                            b {
                                +principal.username
                            }
                        }
                        a {
                            href = "/logout"
                            +"Logout"
                        }
                    } else {
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
            }
            section("row gap-8") {
                search {
                    form(classes = "row gap-8") {
                        hxGet("/html/movies/search")
                        hxTarget("#search-results")
                        hxIndicator("#search-indicator")
                        searchInput {
                            name = "q"
                            placeholder = "The Brutalist"
                            required = true
                        }
                        submitInput {
                            value = "Search"
                        }
                        div(classes = HTMX_INDICATOR) {
                            id = "search-indicator"
                            +"Searching..."
                        }
                    }
                }
                div("row gap-8") {
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
                }
            }
            div {
                div {
                    id = "search-results"
                }
            }
        }
        main {
            block()
        }
    }
}

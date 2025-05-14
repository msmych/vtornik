package uk.matvey.vtornik.web

import kotlinx.html.HTML
import kotlinx.html.MAIN
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.dialog
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.onClick
import kotlinx.html.script
import kotlinx.html.searchInput
import kotlinx.html.section
import kotlinx.html.title
import kotlinx.serialization.json.put
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.SEARCH.Companion.search
import uk.matvey.slon.html.hxBoost
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxTarget
import uk.matvey.slon.html.hxTrigger
import uk.matvey.slon.html.hxVals
import uk.matvey.vtornik.web.auth.UserPrincipal
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS

fun HTML.page(
    config: WebConfig,
    principal: UserPrincipal?,
    title: String,
    activeTab: String?,
    block: MAIN.() -> Unit,
) {
    head {
        title(title)
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1.0"
        }
        link {
            rel = "stylesheet"
            href = "/assets/styles.css"
        }
        link {
            rel = "apple-touch-icon"
            sizes = "180x180"
            href = config.assetUrl("/favicon/apple-touch-icon.png")
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "32x32"
            href = config.assetUrl("/favicon/favicon-32x32.png")
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "16x16"
            href = config.assetUrl("/favicon/favicon-16x16.png")
        }
        link {
            rel = "manifest"
            href = config.assetUrl("/favicon/site.webmanifest")
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
                div("row gap-8") {
                    a(classes = menuTabClasses(activeTab == "vtornik")) {
                        href = "/"
                        this.title = "Vtornik homepage"
                        +"Vtornik"
                    }
                    a(classes = menuTabClasses(activeTab == "now-playing")) {
                        href = "/html/movies/now-playing"
                        this.title = "Now playing in cinema"
                        +"Now"
                    }
                }
                div("row gap-8") {
                    if (principal != null) {
                        button(classes = "menu-tab") {
                            onClick = "openDialog('user-dialog')"
                            this.title = "User settings"
                            +principal.username
                        }
                        dialog {
                            id = "user-dialog"
                            attributes["closedby"] = "any"
                            h1 {
                                +"Logged in as ${principal.username}"
                            }
                            div(classes = "col gap-8") {
                                a(classes = "menu-tab") {
                                    href = "/logout"
                                    this.title = "Logout from Vtornik"
                                    +"Logout"
                                }
                            }
                        }
                    } else {
                        button(classes = "menu-tab") {
                            id = "login-button"
                            this.title = "Login to Vtornik"
                            onClick = "openDialog('login-dialog')"
                            +"Login"
                        }
                        dialog {
                            id = "login-dialog"
                            attributes["closedby"] = "any"
                            h1 {
                                +"Login to Vtornik"
                            }
                            a {
                                href = if (config.profile == Profile.MOCK) {
                                    "/mock/login"
                                } else {
                                    """https://github.com/login/oauth/authorize
                                    |?client_id=${config.githubClientId}
                                    |&redirect_uri=${config.baseUrl()}/github/callback
                                    |""".trimMargin()
                                }
                                +"Login with GitHub"
                            }
                        }
                    }
                }
            }
            section("col gap-8") {
                search {
                    form(classes = "row gap-8") {
                        hxGet("/html/movies/search")
                        hxTrigger("submit, input changed delay:500ms")
                        hxTarget("#search-results")
                        searchInput {
                            name = "q"
                            placeholder = "Perfect Days"
                            required = true
                        }
                        div(classes = HTMX_INDICATOR) {
                            id = "search-indicator"
                            +"Searching..."
                        }
                    }
                }
                div("row gap-8") {
                    principal?.let {
                        STANDARD_TAGS.forEach { tag ->
                            a {
                                href = "/html/movies/search"
                                hxBoost()
                                hxVals {
                                    put("tag", tag.tag)
                                }
                                +tag.label
                            }
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

private fun menuTabClasses(active: Boolean) = if (active) {
    "menu-tab active"
} else {
    "menu-tab"
}

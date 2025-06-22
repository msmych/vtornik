package uk.matvey.vtornik.web.page

import io.ktor.htmx.HxAttributeKeys.Boost
import io.ktor.htmx.HxCss.Indicator
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
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
import uk.matvey.slon.html.SEARCH.Companion.search
import uk.matvey.vtornik.web.auth.UserPrincipal
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile
import uk.matvey.vtornik.web.movie.tag.TagView.Companion.STANDARD_TAGS

@OptIn(ExperimentalKtorApi::class)
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
                        this.title = "Now playing"
                        attributes[Boost] = "true"
                        +"Now"
                    }
                    a(classes = menuTabClasses(activeTab == "upcoming")) {
                        href = "/html/movies/upcoming"
                        this.title = "Coming soon"
                        attributes[Boost] = "true"
                        +"Soon"
                    }
                }
                div("row gap-8") {
                    if (principal != null) {
                        button(classes = "menu-tab") {
                            onClick = "userDialog.showModal()"
                            this.title = "User settings"
                            +principal.username
                        }
                        dialog {
                            id = "userDialog"
                            attributes["closedby"] = "any"
                            button {
                                onClick = "userDialog.close()"
                                +"Close"
                            }
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
                            onClick = "loginDialog.showModal()"
                            +"Login"
                        }
                        dialog {
                            id = "loginDialog"
                            attributes["closedby"] = "any"
                            button {
                                onClick = "loginDialog.close()"
                                +"Close"
                            }
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
                        attributes.hx {
                            get = "/html/movies/search"
                            trigger = "submit, input changed delay:500ms"
                            target = "#search-results"
                        }
                        searchInput {
                            name = "q"
                            placeholder = "Perfect Days"
                            required = true
                        }
                        div(classes = Indicator) {
                            id = "search-indicator"
                            +"Searching..."
                        }
                    }
                }
                div("row gap-8") {
                    principal?.let {
                        STANDARD_TAGS.forEach { tag ->
                            tagFilter(tag.tag, tag.label)
                        }
                        commentedFilter("Commented")
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

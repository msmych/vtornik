package uk.matvey.vtornik.web.movie.search

import io.ktor.htmx.HxCss.Indicator
import io.ktor.htmx.HxSwap.outerHtml
import io.ktor.htmx.html.hx
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.html.HtmlBlockTag
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.matvey.vtornik.person.Person
import uk.matvey.vtornik.web.config.WebConfig
import java.time.LocalDate

class MovieSearchResultItem(
    val id: Long,
    val title: String,
    val originalTitle: String?,
    val releaseDate: LocalDate?,
    val posterUrl: String?,
)

@OptIn(ExperimentalKtorApi::class)
fun HtmlBlockTag.movieSearchResultItemHtml(
    config: WebConfig,
    movie: MovieSearchResultItem,
    directors: List<Person>?,
) {
    div("row gap-8 search-result-item") {
        attributes.hx {
            get = "/html/movies/${movie.id}"
            target = "body"
            pushUrl = "true"
        }
        img(classes = "poster", alt = movie.title) {
            src = movie.posterUrl ?: config.assetUrl("/no-poster.jpg")
        }
        div("col gap-8") {
            b {
                +movie.title
                movie.releaseDate?.let { releaseDate -> +" (${releaseDate.year})" }
            }
            movie.originalTitle?.let { originalTitle ->
                i {
                    +originalTitle
                }
            }
            if (directors != null) {
                +"Directed by "
                +directors.take(3).joinToString { person -> person.name }
                if (directors.size > 3) {
                    +" and ${directors.size - 3} more"
                }
            } else {
                div {
                    attributes.hx {
                        get = "/html/movies/${movie.id}/people"
                        trigger = "intersect once"
                        target = "this"
                        swap = outerHtml
                        pushUrl = "false"
                        vals = Json.encodeToString(buildJsonObject {
                            put("role", "Director")
                        })
                    }
                    div(Indicator) {
                        +"Directed by..."
                    }
                }
            }
        }
        div(Indicator) {
            +"Loading..."
        }
    }
}

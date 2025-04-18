package uk.matvey.vtornik.web.movie.search

import kotlinx.html.HtmlBlockTag
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.serialization.json.put
import uk.matvey.slon.html.HTMX_INDICATOR
import uk.matvey.slon.html.hxGet
import uk.matvey.slon.html.hxPushUrl
import uk.matvey.slon.html.hxSwap
import uk.matvey.slon.html.hxTarget
import uk.matvey.slon.html.hxTrigger
import uk.matvey.slon.html.hxVals
import uk.matvey.vtornik.person.Person
import java.time.LocalDate

class MovieSearchResultItem(
    val id: Long,
    val title: String,
    val originalTitle: String?,
    val releaseDate: LocalDate?,
    val posterUrl: String?,
)

fun HtmlBlockTag.movieSearchResultItemHtml(
    movie: MovieSearchResultItem,
    directors: List<Person>?,
) {
    div("row gap-8 search-result-item") {
        hxGet("/html/movies/${movie.id}")
        hxTarget("body")
        hxPushUrl()
        img(classes = "poster", alt = movie.title) {
            src = movie.posterUrl ?: ""
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
                    hxGet("/html/movies/${movie.id}/people")
                    hxTrigger("intersect once")
                    hxTarget("this")
                    hxSwap("outerHTML")
                    hxPushUrl(false)
                    hxVals {
                        put("role", "Director")
                    }
                    div(HTMX_INDICATOR) {
                        +"Directed by..."
                    }
                }
            }
        }
        div(HTMX_INDICATOR) {
            +"Loading..."
        }
    }
}

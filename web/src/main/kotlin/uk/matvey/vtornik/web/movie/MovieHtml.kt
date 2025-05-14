package uk.matvey.vtornik.web.movie

import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.figcaption
import kotlinx.html.figure
import kotlinx.html.img
import kotlinx.html.title
import uk.matvey.tmdb.TmdbImages
import uk.matvey.vtornik.web.config.WebConfig

class MovieCard(
    val id: Long,
    val title: String,
    val posterPath: String?,
)

fun HtmlBlockTag.movieCardHtml(
    movie: MovieCard,
    tmdbImages: TmdbImages,
    config: WebConfig
) {
    a {
        title = movie.title
        href = "/html/movies/${movie.id}"
        figure {
            img(classes = "poster", alt = movie.title) {
                src = movie.posterPath?.let {
                    tmdbImages.posterUrl(it, "w500")
                } ?: config.assetUrl("/no-poster.jpg")
                alt = movie.title
            }
            figcaption {
                +movie.title
            }
        }
    }
}
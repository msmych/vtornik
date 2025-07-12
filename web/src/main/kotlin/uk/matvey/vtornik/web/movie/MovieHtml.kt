package uk.matvey.vtornik.web.movie

import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.figcaption
import kotlinx.html.figure
import kotlinx.html.img
import kotlinx.html.title
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.MovieService.MovieDetails

fun HtmlBlockTag.movieCardHtml(
    movie: MovieDetails,
    config: WebConfig
) {
    a {
        title = movie.title
        href = "/html/movies/${movie.id}"
        figure {
            img(classes = "poster", alt = movie.title) {
                src = movie.posterUrl ?: config.assetUrl("/no-poster.jpg")
                alt = movie.title
            }
            figcaption {
                +movie.title
            }
        }
    }
}
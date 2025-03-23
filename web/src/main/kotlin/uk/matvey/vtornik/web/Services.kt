package uk.matvey.vtornik.web

import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder.createConnectionPool
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.user.UserRepository
import uk.matvey.vtornik.web.auth.Auth
import uk.matvey.vtornik.web.config.WebConfig

class Services(
    val config: WebConfig,
    val tmdbClient: TmdbClient,
) {

    val db = createConnectionPool(
        config.dbUrl
    ) {
        username = config.dbUsername
        password = config.dbPassword
    }

    val userRepository = UserRepository(db)
    val movieRepository = MovieRepository(db)
    val tagRepository = TagRepository(db)

    val auth = Auth(config)
}
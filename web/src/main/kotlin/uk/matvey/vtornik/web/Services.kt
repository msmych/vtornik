package uk.matvey.vtornik.web

import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder.createConnectionPool
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.movie.MovieRepository
import uk.matvey.vtornik.person.MoviePersonRepository
import uk.matvey.vtornik.person.PersonRepository
import uk.matvey.vtornik.tag.TagRepository
import uk.matvey.vtornik.user.UserRepository
import uk.matvey.vtornik.web.auth.Auth
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.movie.MovieService

class Services(
    val config: WebConfig,
    val tmdbClient: TmdbClient,
) {

    val db = createConnectionPool(
        config.dbUrl
    ) {
        username = config.dbUsername
        password = config.dbPassword
    }.asSuspending

    val userRepository = UserRepository(db)
    val movieRepository = MovieRepository(db)
    val tagRepository = TagRepository(db)
    val personRepository = PersonRepository(db)
    val moviePersonRepository = MoviePersonRepository(db)

    val auth = Auth(config)
    val movieService = MovieService(
        movieRepository,
        moviePersonRepository,
        personRepository,
        tmdbClient,
    )
}
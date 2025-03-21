package uk.matvey.vtornik.web

import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.user.UserRepository
import uk.matvey.vtornik.web.config.VtornikConfig

class Services(
    val tmdbClient: TmdbClient,
    private val config: VtornikConfig,
) {

    val db = PostgreSQLConnectionBuilder.createConnectionPool(
        config.dbUrl
    ) {
        username = config.dbUsername
        password = config.dbPassword
    }

    val userRepository = UserRepository(db)
}
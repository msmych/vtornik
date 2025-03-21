package uk.matvey.vtornik.web

import org.flywaydb.core.Flyway
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.VtornikConfig

fun main() {
    val config = VtornikConfig.fromEnv()
    val tmdbClient = TmdbClient()
    val services = Services(tmdbClient, config)
    val flyway = Flyway.configure()
        .dataSource(config.dbUrl, config.dbUsername, config.dbPassword)
        .cleanDisabled(false)
        .load()
    flyway.clean()
    flyway.migrate()
    val server = server(config, services)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}

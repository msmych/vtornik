package uk.matvey.vtornik.web

import io.ktor.client.engine.cio.CIO
import org.flywaydb.core.Flyway
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.WebConfig

fun main() {
    val config = WebConfig.fromEnv()
    val tmdbClient = TmdbClient(CIO.create { })
    val services = Services(config, tmdbClient)
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

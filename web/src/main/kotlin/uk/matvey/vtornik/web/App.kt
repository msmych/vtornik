package uk.matvey.vtornik.web

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.engine.cio.CIO
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.exception.FlywaySqlException
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.WebConfig

private val log = KotlinLogging.logger("uk.matvey.vtornik.web.App")

fun main() {
    val config = WebConfig.fromEnv()
    val tmdbClient = TmdbClient(CIO.create { }, config.tmdbApiKey)
    val services = Services(config, tmdbClient)
    val flyway = Flyway.configure()
        .dataSource(config.dbUrl, config.dbUsername, config.dbPassword)
        .cleanDisabled(false)
        .load()
    repeat(5) {
        try {
            flyway.clean()
            flyway.migrate()
            return@repeat
        } catch (e: FlywaySqlException) {
            if (it == 4) {
                log.error(e) { "Failed to migrate DB" }
                throw e
            }
            log.warn(e) { "Failed to connect to DB, retrying in 1s" }
            Thread.sleep(1000)
        }
    }
    val server = server(config, services)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}

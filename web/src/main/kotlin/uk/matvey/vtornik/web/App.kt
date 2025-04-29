package uk.matvey.vtornik.web

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.engine.cio.CIO
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.WebConfig

private val log = KotlinLogging.logger("uk.matvey.vtornik.web.App")

fun main() {
    val config = WebConfig.fromEnv()
    val tmdbClient = TmdbClient(CIO.create { }, config.tmdbApiKey)
    log.info { "DB URL len: ${config.dbUrl.length}" }
    val services = Services(config, tmdbClient)
    flywayMigrate(config)
    val server = server(config, services)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}

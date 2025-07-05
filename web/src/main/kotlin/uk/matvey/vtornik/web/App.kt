package uk.matvey.vtornik.web

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.engine.cio.CIO
import uk.matvey.tmdb.TmdbClient
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile

private val log = KotlinLogging.logger("uk.matvey.vtornik.web.App")

fun main() {
    val profile = Profile.valueOf(System.getenv("PROFILE"))
    log.info { "Starting Vtornik web application with profile $profile" }
    val config = WebConfig.from(profile)
    val tmdbClient = TmdbClient(CIO.create { }, config.tmdbApiKey)
    val services = Services(config, tmdbClient)
    flywayMigrate(config)
    val server = server(config, services)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}

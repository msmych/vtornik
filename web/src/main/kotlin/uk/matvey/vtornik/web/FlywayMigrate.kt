package uk.matvey.vtornik.web

import io.github.oshai.kotlinlogging.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.exception.FlywaySqlException
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile


private val log = KotlinLogging.logger("uk.matvey.vtornik.web.FlywayMigrate")

fun flywayMigrate(config: WebConfig) {
    val flyway = Flyway.configure()
        .dataSource(config.dbUrl, config.dbUsername, config.dbPassword)
        .schemas("vtornik")
        .defaultSchema("vtornik")
        .createSchemas(true)
        .cleanDisabled(false)
        .load()
    repeat(5) {
        try {
            if (config.profile != Profile.PROD) {
                flyway.clean()
            }
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
}
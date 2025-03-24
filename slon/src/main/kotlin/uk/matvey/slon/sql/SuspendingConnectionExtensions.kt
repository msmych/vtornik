package uk.matvey.slon.sql

import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.postgresql.exceptions.GenericDatabaseException
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger("com.github.jasync.sql.db.SuspendingConnection")

suspend fun SuspendingConnection.execute(sql: String, params: List<Any?>) = try {
    sendPreparedStatement(sql, params)
} catch (e: GenericDatabaseException) {
    log.error(e) { "Failed to execute SQL: $sql" }
    throw e
}
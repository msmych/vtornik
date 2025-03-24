package uk.matvey.slon

import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder.createConnectionPool
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer

open class PostgresTestSetup {

    companion object {
        val postgres = PostgreSQLContainer("postgres")
        lateinit var db: SuspendingConnection

        @BeforeAll
        @JvmStatic
        fun setupPostgres() {
            postgres.start()
            db = createConnectionPool(postgres.jdbcUrl) {
                username = postgres.username
                password = postgres.password
            }.asSuspending
        }

        @AfterAll
        @JvmStatic
        fun teardownPostgres() {
            postgres.stop()
        }
    }
}
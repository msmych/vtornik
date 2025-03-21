package uk.matvey.slon

import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder.createConnectionPool
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer

open class PostgresTestSetup {

    companion object {
        val postgres = PostgreSQLContainer("postgres")
        lateinit var db: ConnectionPool<PostgreSQLConnection>

        @BeforeAll
        @JvmStatic
        fun setupPostgres() {
            postgres.start()
            db = createConnectionPool(postgres.jdbcUrl) {
                username = postgres.username
                password = postgres.password
            }
        }

        @AfterAll
        @JvmStatic
        fun teardownPostgres() {
            postgres.stop()
        }
    }
}
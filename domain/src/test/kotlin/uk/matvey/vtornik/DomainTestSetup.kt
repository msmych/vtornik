package uk.matvey.vtornik

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestSetup

open class DomainTestSetup : PostgresTestSetup() {

    companion object {

        @BeforeAll
        @JvmStatic
        fun setupDomain() {
            val flyway = Flyway.configure()
                .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
                .load()
            flyway.migrate()
        }
    }
}
package uk.matvey.vtornik.web

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServerTest : WebTestSetup() {

    @Test
    fun `should return OK on health check`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/health")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo("OK")
    }
}
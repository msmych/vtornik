package uk.matvey.vtornik.web

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IndexTest : WebTestSetup() {

    @Test
    fun `should return index page`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("<html>")
            .contains("<head>")
            .contains("<body")
    }

    @Test
    fun `should include username if logged in`() = testApplication {
        // given
        application {
            testServerModule()
        }

        // when
        val rs = client.get("/") {
            appendJwtCookie()
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("Logged in as")
            .contains("Logout")
    }
}
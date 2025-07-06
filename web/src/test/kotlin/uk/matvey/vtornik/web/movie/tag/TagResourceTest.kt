package uk.matvey.vtornik.web.movie.tag

import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.Parameters
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.tag.Tag
import uk.matvey.vtornik.web.WebTestSetup
import kotlin.random.Random

class TagResourceTest : WebTestSetup() {

    @Test
    fun `should set tag value`() = testApplication {
        // given
        application {
            testServerModule()
        }
        val userId = Random.nextInt()
        val movieId = Random.nextLong()

        // when
        val rs = client.put("/html/movies/$movieId/tags/WATCHLIST") {
            appendJwtCookie(userId)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("value", "true")
                    }
                )
            )
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        val tag = services.tagRepository.findByUserIdMovieIdAndType(userId, movieId, Tag.Type.WATCHLIST)
        assertThat(tag).isNotNull
        assertThat(tag?.value).isEqualTo(JsonPrimitive(true))
        assertThat(rs.bodyAsText()).contains("checked").contains("false")
    }
}
package uk.matvey.vtornik.web

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.CookieEncoding
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

fun Routing.githubRouting(clientId: String, appSecret: String) {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    val clientSecret = System.getenv("GITHUB_CLIENT_SECRET")
    route("/github") {
        get("/callback") {
            val code = call.parameters.getOrFail("code")
            val tokenData = httpClient.post("https://github.com/login/oauth/access_token") {
                setBody(
                    FormDataContent(
                        parameters {
                            append("client_id", clientId)
                            append("client_secret", clientSecret)
                            append("code", code)
                        }
                    )
                )
            }.body<GithubAccessTokenResponse>()
            val userInfo = httpClient.get("https://api.github.com/user") {
                bearerAuth(tokenData.accessToken)
            }.body<GithubUserInfoResponse>()
            val jwt = JWT.create()
                .withIssuer("vtornik")
                .withAudience("vtornik")
                .withSubject(userInfo.id.toString())
                .withClaim("username", userInfo.login)
                .withClaim("name", userInfo.name)
                .withExpiresAt(Instant.now().plus(7.days.toJavaDuration()))
                .sign(Algorithm.HMAC256(appSecret))
            call.response.cookies.append(
                name = "jwt",
                value = jwt,
                encoding = CookieEncoding.RAW,
                expires = GMTDate() + 7.days,
                path = "/",
                httpOnly = true,
                extensions = mapOf(SAMESITE to "Lax")
            )
            call.respondRedirect("http://localhost:8080")
        }
    }
}

@Serializable
data class GithubAccessTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
)

@Serializable
data class GithubUserInfoResponse(
    val id: Long,
    val login: String,
    val name: String,
)

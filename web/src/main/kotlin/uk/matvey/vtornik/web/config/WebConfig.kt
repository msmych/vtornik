package uk.matvey.vtornik.web.config

import com.auth0.jwt.algorithms.Algorithm
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import io.ktor.server.request.host
import io.ktor.server.routing.RoutingRequest

class WebConfig(
    val profile: Profile,
    val appSecret: String,
    val dbUrl: String,
    val dbUsername: String,
    val dbPassword: String,
    val assetsUrl: String,
    val githubClientId: String?,
    val githubClientSecret: String?,
    val tmdbApiKey: String,
) {

    enum class Profile {
        PROD,
        DEV,
        MOCK,
    }

    fun baseUrl(request: RoutingRequest) = when (profile) {
        Profile.PROD -> "https://${request.host()}"
        Profile.DEV,
        Profile.MOCK -> "http://${request.host()}:${request.local.remotePort}"
    }

    fun jwtAlgorithm(): Algorithm = Algorithm.HMAC256(appSecret)

    fun assetUrl(path: String) = "$assetsUrl/$path"

    fun githubClientId() = requireNotNull(githubClientId) { "GitHub client ID is not set" }

    fun githubClientSecret() = requireNotNull(githubClientSecret) { "GitHub client secret is not set" }

    companion object {

        fun from(profile: Profile): WebConfig {
            return when (profile) {
                Profile.PROD -> {
                    SecretManagerServiceClient.create().use { client ->
                        WebConfig(
                            profile = profile,
                            appSecret = client.getSecretValue("APP_SECRET"),
                            dbUrl = client.getSecretValue("DB_URL"),
                            dbUsername = client.getSecretValue("DB_USERNAME"),
                            dbPassword = client.getSecretValue("DB_PASSWORD"),
                            assetsUrl = System.getenv("ASSETS_URL"),
                            githubClientId = client.getSecretValue("GITHUB_CLIENT_ID"),
                            githubClientSecret = client.getSecretValue("GITHUB_CLIENT_SECRET"),
                            tmdbApiKey = client.getSecretValue("TMDB_API_KEY"),
                        )
                    }
                }
                else -> WebConfig(
                    profile = profile,
                    appSecret = System.getenv("APP_SECRET"),
                    dbUrl = System.getenv("DB_URL"),
                    dbUsername = System.getenv("DB_USERNAME"),
                    dbPassword = System.getenv("DB_PASSWORD"),
                    assetsUrl = System.getenv("ASSETS_URL"),
                    githubClientId = System.getenv("GITHUB_CLIENT_ID"),
                    githubClientSecret = System.getenv("GITHUB_CLIENT_SECRET"),
                    tmdbApiKey = System.getenv("TMDB_API_KEY")
                )
            }
        }

        private fun SecretManagerServiceClient.getSecretValue(key: String): String {
            val projectId = System.getenv("GCLOUD_PROJECT_ID")
            return accessSecretVersion("projects/$projectId/secrets/$key/versions/latest")
                .payload.data.toStringUtf8()
        }
    }
}

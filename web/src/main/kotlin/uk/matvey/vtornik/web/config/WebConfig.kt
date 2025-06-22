package uk.matvey.vtornik.web.config

import com.auth0.jwt.algorithms.Algorithm

class WebConfig(
    val profile: Profile,
    val appSecret: String,
    val jksPass: String?,
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

    fun baseUrl() = when (profile) {
        Profile.PROD -> "https://matvey.uk"
        Profile.DEV -> "http://localhost:8080"
        Profile.MOCK -> "http://localhost:8080"
    }

    fun jwtAlgorithm(): Algorithm = Algorithm.HMAC256(appSecret)

    fun jksPass() = requireNotNull(jksPass) { "JKS password is not set" }

    fun assetUrl(path: String) = "$assetsUrl/$path"

    fun githubClientId() = requireNotNull(githubClientId) { "GitHub client ID is not set" }

    fun githubClientSecret() = requireNotNull(githubClientSecret) { "GitHub client secret is not set" }

    companion object {

        fun fromEnv(): WebConfig {
            return WebConfig(
                profile = Profile.valueOf(System.getenv("PROFILE")),
                appSecret = System.getenv("APP_SECRET"),
                jksPass = System.getenv("JKS_PASS"),
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
}

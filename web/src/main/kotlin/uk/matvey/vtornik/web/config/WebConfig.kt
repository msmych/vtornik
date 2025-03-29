package uk.matvey.vtornik.web.config

import com.auth0.jwt.algorithms.Algorithm

class WebConfig(
    val profile: Profile,
    val appSecret: String,
    val dbUrl: String,
    val dbUsername: String,
    val dbPassword: String,
    val githubClientId: String?,
    val tmdbApiKey: String,
) {

    enum class Profile {
        DEV,
        MOCK,
    }

    fun jwtAlgorithm(): Algorithm = Algorithm.HMAC256(appSecret)

    companion object {

        fun fromEnv(): WebConfig {
            return WebConfig(
                profile = Profile.valueOf(System.getenv("PROFILE")),
                appSecret = System.getenv("APP_SECRET"),
                dbUrl = System.getenv("DB_URL"),
                dbUsername = System.getenv("DB_USERNAME"),
                dbPassword = System.getenv("DB_PASSWORD"),
                githubClientId = System.getenv("GITHUB_CLIENT_ID"),
                tmdbApiKey = System.getenv("TMDB_API_KEY")
            )
        }
    }
}
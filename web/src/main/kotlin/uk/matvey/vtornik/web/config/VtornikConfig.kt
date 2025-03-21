package uk.matvey.vtornik.web.config

import com.auth0.jwt.algorithms.Algorithm

class VtornikConfig(
    val appSecret: String,
    val dbUrl: String,
    val dbUsername: String,
    val dbPassword: String,
) {

    fun jwtAlgorithm(): Algorithm = Algorithm.HMAC256(appSecret)

    companion object {

        fun fromEnv(): VtornikConfig {
            return VtornikConfig(
                appSecret = System.getenv("APP_SECRET"),
                dbUrl = System.getenv("DB_URL"),
                dbUsername= System.getenv("DB_USERNAME"),
                dbPassword = System.getenv("DB_PASSWORD"),
            )
        }
    }
}
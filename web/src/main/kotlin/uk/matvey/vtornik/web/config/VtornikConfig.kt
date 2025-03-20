package uk.matvey.vtornik.web.config

import com.auth0.jwt.algorithms.Algorithm

class VtornikConfig(
    val appSecret: String,
) {

    fun jwtAlgorithm(): Algorithm = Algorithm.HMAC256(appSecret)

    companion object {

        fun fromEnv(): VtornikConfig {
            return VtornikConfig(
                appSecret = System.getenv("APP_SECRET"),
            )
        }
    }
}
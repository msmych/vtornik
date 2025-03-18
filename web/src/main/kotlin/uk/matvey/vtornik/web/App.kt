package uk.matvey.vtornik.web

import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    val server = embeddedServer(
        factory = Netty,
        environment = applicationEnvironment { },
        configure = {
            connector {
                port = 8080
            }
        },
    ) {
        routing {
            get("/health") {
                call.respondText("OK")
            }
        }
    }
    server.start(wait = true)
}
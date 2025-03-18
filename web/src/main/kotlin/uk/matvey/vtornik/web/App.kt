package uk.matvey.vtornik.web

import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.title

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
            get {
                call.respondHtml {
                    head {
                        title = "Vtornik"
                    }
                    body {
                        h1 {
                            +"Vtornik"
                        }
                    }
                }
            }
        }
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}
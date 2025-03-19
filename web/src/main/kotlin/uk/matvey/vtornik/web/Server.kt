package uk.matvey.vtornik.web

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import uk.matvey.tmdb.TmdbClient

fun server(): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    val server = embeddedServer(
        factory = Netty,
        environment = applicationEnvironment { },
        configure = {
            connector {
                port = 8080
            }
        },
    ) {
        serverModule(TmdbClient())
    }
    return server
}


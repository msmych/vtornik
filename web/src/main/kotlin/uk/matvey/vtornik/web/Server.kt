package uk.matvey.vtornik.web

import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import uk.matvey.vtornik.web.config.WebConfig
import uk.matvey.vtornik.web.config.WebConfig.Profile.PROD
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun server(
    config: WebConfig,
    services: Services
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    val server = embeddedServer(
        factory = Netty,
        environment = applicationEnvironment { },
        configure = {
            if (config.profile == PROD) {
                val jksPass = config.jksPass().toCharArray()
                val keyStoreFile = File("/certs/keystore.jks")
                sslConnector(
                    keyStore = KeyStore.getInstance("JKS").apply {
                        load(FileInputStream(keyStoreFile), jksPass)
                    },
                    keyAlias = "vtornik-p12",
                    privateKeyPassword = { jksPass },
                    keyStorePassword = { jksPass },
                ) {
                    port = 8443
                    keyStorePath = keyStoreFile
                }
            } else {
                connector {
                    port = 8080
                }
            }
        },
    ) {
        serverModule(config, services)
    }
    return server
}


package uk.matvey.vtornik.web

fun main() {
    val server = server()
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1000, 1000)
    })
    server.start(wait = true)
}

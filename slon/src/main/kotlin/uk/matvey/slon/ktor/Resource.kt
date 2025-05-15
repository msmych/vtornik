package uk.matvey.slon.ktor

import io.ktor.server.routing.Route

interface Resource {

    fun Route.routing()
}
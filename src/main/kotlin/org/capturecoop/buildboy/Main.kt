package org.capturecoop.buildboy

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import org.capturecoop.buildboy.routes.projectsRoute
import org.capturecoop.buildboy.routes.testRoute

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
        }
        install(ContentNegotiation) {
            json()
        }
        routing {
            testRoute()
            projectsRoute()
        }
    }.start(wait = true)
}
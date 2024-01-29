package org.capturecoop.buildboy.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.testRoute() {
    get("/test") {
        call.respondText("Hello World!")
    }
}
package org.capturecoop.buildboy.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.ArrayList
import java.util.UUID

@Serializable
data class Project(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String
)


object ProjectManager {
    private val projects = ArrayList<Project>()

    fun allProjects() = projects
    fun getProject(name: String = "", uuid: String = ""): Project? = projects.firstOrNull { it.name == name || it.uuid == uuid }
    fun addProject(project: Project) = projects.add(project)
}

fun Route.projectsRoute() {
    route("/projects") {
        //Get all projects
        get("/") {
            call.respond(ProjectManager.allProjects())
        }

        //Get project by name or uuid
        get("/{name}") {
            val name = call.parameters["name"]!!
            val project = ProjectManager.getProject(name = name, uuid = name) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(project)
        }

        //Get all versions for a given project
        get("/{name}/versions/") {
            call.respondText("Version!")
        }

        //Create project
        post("/") {
            val new: Project = call.receiveNullable() ?: return@post call.respondText("", status = HttpStatusCode.BadRequest)
            if(ProjectManager.getProject(name = new.name, uuid = new.uuid) != null) return@post call.respondText("UUID and Name should be unique!", status = HttpStatusCode.BadRequest)
            ProjectManager.addProject(new)
            call.respond(HttpStatusCode.OK)
        }


    }
}
package com.gmail.marcosav2010.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Route.login() {
    post<Login> {
        //val session = call.sessions.get<Session>() ?: Session()
        //call.sessions.set(session.copy(count = session.count + 1))
        call.respond(HttpStatusCode.OK)
    }

    get("/logout") {
        call.respondRedirect("/")
    }
}

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val user: String, val password: String)
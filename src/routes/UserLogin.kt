package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.login() {

    val userService by di().instance<UserService>()

    post<Login> {
        //val session = call.sessions.get<Session>() ?: Session()
        //call.sessions.set(session.copy(count = session.count + 1))
        val hashedPassword = it.password
        val matched = userService.findByUsernameAndPassword(it.username, hashedPassword)
        call.respond(HttpStatusCode.OK)
    }

    get("/logout") {
        // delete session
        call.sessions.clear<Session>()
        call.respond(HttpStatusCode.OK)
    }
}

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val username: String, val password: String)
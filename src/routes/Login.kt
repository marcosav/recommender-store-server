package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.login() {

    val userService by di().instance<UserService>()

    post<Login> {
        //val session = call.sessions.get<Session>() ?: Session()
        //call.sessions.set(session.copy(count = session.count + 1))
        val matched = userService.findByUsernameAndPassword(it.username, it.password)
        call.respond(HttpStatusCode.OK)
    }

    get("/logout") {
        // delete session
        call.respond(HttpStatusCode.OK)
    }
}

@KtorExperimentalLocationsAPI
@Location("/login")
data class Login(val username: String, val password: String)
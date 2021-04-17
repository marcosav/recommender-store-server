package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

@KtorExperimentalLocationsAPI
fun Route.auth() {

    val authenticationService by closestDI().instance<AuthenticationService>()

    post("/auth") {
        val token = authenticationService.token(UUID.randomUUID().toString(), emptyList())
        call.respond(token)
    }
}

@KtorExperimentalLocationsAPI
fun Route.reauth() {

    val authenticationService by closestDI().instance<AuthenticationService>()

    post("/reauth") {
        call.respond(authenticationService.token(session))
    }
}
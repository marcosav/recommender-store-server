package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.safeSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import java.util.*

@KtorExperimentalLocationsAPI
fun Route.auth() {

    val authenticationService by di().instance<AuthenticationService>()

    post("/auth") {
        if (safeSession != null)
            return@post call.respond(HttpStatusCode.NoContent)

        val token = authenticationService.token(UUID.randomUUID().toString())
        call.respond(token)
    }
}
package com.gmail.marcosav2010.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Application.setupRoutes() {
    routing {
        auth()

        authenticate {
            login()
            signup()
            users()
            product()
            cart()
            favorites()
        }
    }
}
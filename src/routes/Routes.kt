package com.gmail.marcosav2010.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Application.setupRoutes() {
    routing {
        login()
        signup()
        product()
    }
}
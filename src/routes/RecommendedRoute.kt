package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.RecommenderService
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.recommended() {

    val recommenderService by closestDI().instance<RecommenderService>()

    get("/recommended") {
        // TODO: User
        val products = recommenderService.getFor(session.userId ?: 1)
        call.respond(products)
    }
}
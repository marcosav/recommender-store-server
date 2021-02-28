package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.validators.ValidationException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

class ForbiddenException : Exception()

class NotFoundException : Exception()

fun StatusPages.Configuration.setupExceptionHandler() {
    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound)
    }

    exception<ForbiddenException> {
        call.respond(HttpStatusCode.Forbidden)
    }

    exception<ValidationException> {
        call.respond(HttpStatusCode.BadRequest, it.result)
    }
}
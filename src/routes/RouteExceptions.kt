package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.validators.ValidationException
import db.PagingException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

class ForbiddenException(val msg: Any? = null) : Exception()

class NotFoundException(val msg: Any? = null) : Exception()

class UnauthorizedException(val msg: Any? = null) : Exception()

class BadRequestException(val msg: Any? = null) : Exception()

fun StatusPages.Configuration.setupExceptionHandler() {
    exception<NotFoundException> {
        call.respondError(HttpStatusCode.NotFound, it.msg)
    }

    exception<ForbiddenException> {
        call.respondError(HttpStatusCode.Forbidden, it.msg)
    }

    exception<UnauthorizedException> {
        call.respondError(HttpStatusCode.Unauthorized, it.msg)
    }

    exception<BadRequestException> {
        call.respondError(HttpStatusCode.BadRequest, it.msg)
    }

    exception<PagingException> {
        call.respondError(HttpStatusCode.BadRequest, null)
    }

    exception<ValidationException> {
        call.respondError(HttpStatusCode.BadRequest, it.result)
    }
}

private suspend fun ApplicationCall.respondError(httpStatusCode: HttpStatusCode, error: Any?) =
    if (error == null) respond(httpStatusCode) else respond(httpStatusCode, Error(error))

private class Error(val error: Any)
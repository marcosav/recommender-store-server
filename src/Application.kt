package com.gmail.marcosav2010

import com.gmail.marcosav2010.db.setupDB
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.routes.setupRoutes
import com.gmail.marcosav2010.services.setupServices
import com.gmail.marcosav2010.validators.setupValidators
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import org.kodein.di.ktor.di
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val logger: Logger = LoggerFactory.getLogger("Application")

@KtorExperimentalLocationsAPI
@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    setupDB()

    if (!testing)
        install(HttpsRedirect) {
            permanentRedirect = true
        }

    install(ContentNegotiation) { gson {} }
    install(Locations)
    install(Compression) { gzip(); deflate() }

    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)

        /*header(HttpHeaders.Authorization)
        allowCredentials = true*/

        //anyHost() // Don't do this in production if possible. Try to limit it.
    }

    install(Sessions) {
        header<Session>("session-id", storage = SessionStorageMemory())
    }

    install(Authentication) {
    }

    di {
        setupServices()
        setupValidators()
    }

    setupRoutes()
}


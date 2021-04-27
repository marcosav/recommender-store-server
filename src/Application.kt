package com.gmail.marcosav2010

import com.gmail.marcosav2010.api.setupAPIs
import com.gmail.marcosav2010.db.setupDB
import com.gmail.marcosav2010.routes.setupExceptionHandler
import com.gmail.marcosav2010.routes.setupRoutes
import com.gmail.marcosav2010.services.setupJWT
import com.gmail.marcosav2010.services.setupServices
import com.gmail.marcosav2010.validators.setupValidators
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    setupDB()

    install(ContentNegotiation) { gson {} }
    install(Locations)
    install(Compression) { gzip(); deflate() }

    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)

        header(HttpHeaders.Authorization)

        configureHosts()
    }

    di {
        setupAPIs()
        setupServices()
        setupValidators()
    }

    install(Authentication) {
        setupJWT(closestDI())
    }

    install(StatusPages) {
        setupExceptionHandler()
    }

    setupRoutes()
}

fun CORS.Configuration.configureHosts() =
    kotlin.runCatching { System.getenv(Constants.ALLOWED_HOSTS_ENV) }.getOrNull()?.let {
        it.split(",").filter { h -> h.isNotBlank() }
            .forEach { h -> if (h.startsWith("localhost")) host(h) else host(h, listOf("https")) }
    }
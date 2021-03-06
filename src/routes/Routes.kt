package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.utils.ImageSaver
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.routing.*

const val API_ROUTE = "/api/v${Constants.API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.setupRoutes() {
    routing {
        authenticate {
            static(ImageSaver.STATIC_FILES_ROUTE) {
                files(ImageSaver.IMG_FOLDER_ROUTE)
            }
        }

        route(API_ROUTE) {
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
}
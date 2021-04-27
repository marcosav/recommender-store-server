package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.utils.ImageHandler
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.routing.*

const val BASE_ROUTE = "/v${Constants.API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.setupRoutes() {
    routing {
        route(BASE_ROUTE) {
            auth()

            authenticate {
                static(ImageHandler.STATIC_FILES_ROUTE) {
                    static(ImageHandler.IMG_FOLDER_ROUTE) {
                        files(ImageHandler.IMG_PATH)
                    }
                }

                reauth()
                login()
                signup()
                users()
                product()
                productReports()
                cart()
                favorites()
                checkout()
                orders()
                recommended()
            }
        }
    }
}
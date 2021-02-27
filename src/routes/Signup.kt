package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.services.UserService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.signup() {

    val userService by di().instance<UserService>()

    route("/signup") {
        post<User> {
            // validate (length, uniqueness)
            userService.add(it)
            call.respond(HttpStatusCode.OK)
        }
    }
}

/*@KtorExperimentalLocationsAPI
@Location("/signup")
data class Signup(val name: String, val surname: String, val surname: String, val surname: String)*/
package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.validators.SignupValidator
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
    val signupValidator by di().instance<SignupValidator>()

    route("/signup") {
        post<Signup> {
            signupValidator.validate(it)
            userService.add(it.toUser())
            call.respond(HttpStatusCode.OK)
        }
    }
}

data class Signup(
    val name: String,
    val surname: String,
    val email: String,
    val repeatedEmail: String,
    val password: String,
    val repeatedPassword: String,
    val profileImgUri: String? = null,
    val nickname: String
) {
    fun toUser() =
        User(
            null,
            name,
            surname,
            email,
            password,
            profileImgUri,
            nickname
        )
}
package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.utils.ImageSaver
import com.gmail.marcosav2010.validators.UserFormValidator
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
    val userFormValidator by di().instance<UserFormValidator>()

    route("/signup") {
        post<UserForm> {
            userFormValidator.validate(it)

            it.profileImage?.let { i -> it.profileImage = ImageSaver.process(i, it.profileImageExt!!) }

            userService.add(it.toUser())
            call.respond(HttpStatusCode.OK)
        }
    }

    route("/profile") {
        put<UserForm> {
            it.editing = true
            userFormValidator.validate(it)

            it.profileImage?.let { i -> it.profileImage = ImageSaver.process(i, it.profileImageExt!!) }

            userService.update(it.toUser())
            call.respond(HttpStatusCode.OK)
        }
    }
}

data class UserForm(
    val name: String,
    val surname: String,
    var email: String,
    val repeatedEmail: String,
    val password: String,
    val repeatedPassword: String,
    val nickname: String,
    var profileImage: String? = null,
    var profileImageExt: String? = null,
) {
    var editing: Boolean = false

    fun toUser() =
        User(
            null,
            name,
            surname,
            email,
            password,
            nickname,
            profileImage
        )
}
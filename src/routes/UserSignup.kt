package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.utils.ImageSaver
import com.gmail.marcosav2010.validators.UserEditFormValidator
import com.gmail.marcosav2010.validators.UserRegisterFormValidator
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
    val userRegisterFormValidator by di().instance<UserRegisterFormValidator>()
    val userEditFormValidator by di().instance<UserEditFormValidator>()

    route("/signup") {
        post<UserForm> {
            userRegisterFormValidator.validate(it)

            it.profileImage?.let { i -> it.profileImage = ImageSaver.process(i, it.profileImageExt!!) }

            userService.add(it.toUser())
            call.respond(HttpStatusCode.OK)
        }
    }

    route("/edit") {
        put<UserForm> {
            userEditFormValidator.validate(it)

            it.profileImage?.let { i -> it.profileImage = ImageSaver.process(i, it.profileImageExt!!) }

            userService.update(it.toUser())
            call.respond(HttpStatusCode.OK)
        }
    }
}

data class UserForm(
    val id: Long? = null,
    val name: String,
    val surname: String,
    val email: String = "",
    val repeatedEmail: String = "",
    val password: String,
    val repeatedPassword: String,
    val nickname: String,
    val description: String,
    var profileImage: String? = null,
    var profileImageExt: String? = null,
) {
    fun toUser() =
        User(
            id,
            name,
            surname,
            email,
            password,
            nickname,
            description,
            profileImage
        )
}
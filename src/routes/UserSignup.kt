package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.utils.ImageHandler
import com.gmail.marcosav2010.utils.OversizeImageException
import com.gmail.marcosav2010.validators.ImageValidator
import com.gmail.marcosav2010.validators.UserEditFormValidator
import com.gmail.marcosav2010.validators.UserRegisterFormValidator
import com.gmail.marcosav2010.validators.Validator
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.signup() {

    val userService by di().instance<UserService>()
    val authenticationService by di().instance<AuthenticationService>()

    val userRegisterFormValidator by di().instance<UserRegisterFormValidator>()
    val userEditFormValidator by di().instance<UserEditFormValidator>()
    val imageValidator by di().instance<ImageValidator>()

    suspend fun PipelineContext<Unit, ApplicationCall>.handleMultipart(
        validator: Validator<UserForm>,
        success: suspend (Pair<UserForm, String?>) -> Unit
    ) {
        val multipart = call.receiveMultipart()

        var form: UserForm? = null
        var image: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name != "form") return@forEachPart

                    form = parseForm(part.value)

                    validator.validate(form!!)
                }
                is PartData.FileItem -> {
                    if (part.name != "file") return@forEachPart

                    part.originalFileName?.let {
                        imageValidator.validate(it)
                        image = try {
                            ImageHandler.process(part)
                        } catch (ex: OversizeImageException) {
                            imageValidator.onOversize()
                        }
                    }
                }
            }

            part.dispose()
        }

        success(Pair(form!!, image))
    }

    post("/signup") {
        session.userId?.let { return@post call.respond(HttpStatusCode.NoContent) }

        handleMultipart(userRegisterFormValidator) {
            userService.add(it.first.toUser(it.second))
            call.respond(HttpStatusCode.OK)
        }
    }

    put("/profile/edit") {
        assertIdentified()

        handleMultipart(userEditFormValidator) {
            val form = it.first
            userService.update(form.toUser(if (form.deletePhoto) "" else it.second))

            val adminChange = form.id != session.userId
            if (!adminChange)
                session.username = form.nickname

            call.respond(if (adminChange) HttpStatusCode.OK else mapOf("token" to authenticationService.token(session)))
        }
    }
}

private fun parseForm(str: String): UserForm = Gson().fromJson(str, UserForm::class.java)

data class UserForm(
    var id: Long? = null,
    val name: String,
    val surname: String,
    val email: String = "",
    val repeatedEmail: String = "",
    val password: String,
    val repeatedPassword: String,
    val nickname: String,
    val description: String,
    val deletePhoto: Boolean = false
) {
    fun toUser(profileImage: String?) =
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
package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.UserService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.users() {

    route("/user") {

        val userService by di().instance<UserService>()

        get<UserSearch> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val users = userService.findByNickname(it.query, Constants.PRODUCTS_PER_PAGE, offset)

            call.respond(users)
        }

        get<UserProfile> {
            val user = userService.findById(it.id)?.toPublicUser() ?: NotFoundException()
            call.respond(user)
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class UserSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int? = null)

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class UserProfile(val id: Long)
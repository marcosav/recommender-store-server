package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.FavoriteService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.session
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
        val favoriteService by di().instance<FavoriteService>()

        get<UserSearch> {
            val offset = it.page * Constants.PRODUCTS_PER_PAGE
            val users = userService.findByNickname(it.query, Constants.PRODUCTS_PER_PAGE, offset)
            session.userId?.let {
                users.items.onEach { u -> u.fav = favoriteService.isFavoriteVendor(u.id, it) }
            }

            call.respond(users)
        }

        get<UserProfile> {
            val user = userService.findById(it.id) ?: throw NotFoundException()
            if (!it.detailed) {
                val publicUser = user.toPublicUser()
                session.userId?.let { uid -> publicUser.fav = favoriteService.isFavoriteVendor(publicUser.id, uid) }
                call.respond(publicUser)
            } else {
                assertIdentified()

                if (session.userId != it.id && !session.isAdmin)
                    throw ForbiddenException()

                user.removeConfidential()
                call.respond(user)
            }
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class UserSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int? = null)

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class UserProfile(val id: Long, val detailed: Boolean = false)
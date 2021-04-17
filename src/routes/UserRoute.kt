package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.users() {

    route("/user") {

        val userService by closestDI().instance<UserService>()
        val favoriteService by closestDI().instance<FavoriteService>()
        val orderService by closestDI().instance<OrderService>()

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

                if (session.userId != user.id && !session.isAdmin)
                    throw ForbiddenException()

                user.removeConfidential()
                call.respond(user)
            }
        }

        delete<DeleteUser> {
            assertAdmin()

            val user = userService.findById(it.id) ?: throw NotFoundException()

            userService.delete(user.id!!)

            call.respond(HttpStatusCode.OK)
        }

        get<UserAddressPath> {
            assertIdentified()

            if (session.userId != it.id && !session.isAdmin)
                throw ForbiddenException()

            val addresses = orderService.findLastAddressesForUser(it.id)

            call.respond(addresses)
        }
    }
}

@KtorExperimentalLocationsAPI
@Location("/list")
data class UserSearch(val query: String, val page: Int = 0, val category: Long? = null, val order: Int? = null)

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class UserProfile(val id: Long, val detailed: Boolean = false)

@KtorExperimentalLocationsAPI
@Location("/{id}")
data class DeleteUser(val id: Long)

@KtorExperimentalLocationsAPI
@Location("/{id}/addresses")
data class UserAddressPath(val id: Long)
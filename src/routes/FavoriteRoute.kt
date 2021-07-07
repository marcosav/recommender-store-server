package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.favorites() {

    val favoriteService by closestDI().instance<FavoriteService>()
    val collectorService by closestDI().instance<FeedbackService>()
    val productService by closestDI().instance<ProductService>()
    val userService by closestDI().instance<UserService>()

    route("/favorites") {
        route("/products") {
            get<FavoriteList> {
                assertIdentified()

                val offset = it.page * Constants.PRODUCTS_PER_PAGE
                val products = favoriteService.findProductsByUser(
                    session.userId!!,
                    Constants.PRODUCTS_PER_PAGE,
                    offset
                ).apply { items.onEach { p -> p.fav = true } }

                call.respond(products)
            }

            post<AddFavorite> {
                assertIdentified()

                val userId = session.userId!!
                val product = productService.findById(it.id)
                if (product?.userId == userId)
                    return@post call.respond(HttpStatusCode.NoContent)

                if (product?.hidden != false)
                    throw NotFoundException()

                favoriteService.addProduct(it.id, userId)
                collectorService.collectFavorite(session, it.id)

                call.respond(HttpStatusCode.OK)
            }

            delete<DeleteFavorite> {
                assertIdentified()

                favoriteService.removeProduct(it.id, session.userId!!)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/vendors") {
            get<FavoriteList> {
                assertIdentified()

                val offset = it.page * Constants.PRODUCTS_PER_PAGE
                val vendors = favoriteService.findVendorsByUser(
                    session.userId!!,
                    Constants.PRODUCTS_PER_PAGE,
                    offset
                )

                call.respond(vendors)
            }

            post<AddFavorite> {
                assertIdentified()

                val userId = session.userId!!
                if (it.id == userId)
                    return@post call.respond(HttpStatusCode.NoContent)

                val vendor = userService.findById(it.id) ?: throw NotFoundException()

                favoriteService.addVendor(vendor.id!!, userId)
                call.respond(HttpStatusCode.OK)
            }

            delete<DeleteFavorite> {
                assertIdentified()

                favoriteService.removeVendor(it.id, session.userId!!)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

data class FavoriteList(val page: Int = 0, val order: Int? = null)

data class DeleteFavorite(val id: Long)

data class AddFavorite(val id: Long)
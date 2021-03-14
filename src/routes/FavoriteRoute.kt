package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.FavoriteService
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.UserService
import com.gmail.marcosav2010.services.session
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.favorites() {

    route("/favorites") {

        val favoriteService by di().instance<FavoriteService>()

        route("/products") {
            val productService by di().instance<ProductService>()

            get<FavoriteList> {
                val offset = it.page * Constants.PRODUCTS_PER_PAGE
                val products = favoriteService.findProductsByUser(
                    session.userId!!,
                    Constants.PRODUCTS_PER_PAGE,
                    offset
                ).apply { items.onEach { p -> p.fav = true } }

                call.respond(products)
            }

            post<AddFavorite> {
                val userId = session.userId!!
                val product = productService.findById(it.id)
                if (product?.userId == userId)
                    throw BadRequestException()

                if (product?.hidden != false)
                    throw NotFoundException()

                favoriteService.addProduct(it.id, userId)
                call.respond(HttpStatusCode.OK)
            }

            delete<DeleteFavorite> {
                favoriteService.removeProduct(it.id, session.userId!!)
                call.respond(HttpStatusCode.OK)
            }
        }

        route("/vendors") {
            val userService by di().instance<UserService>()

            get<FavoriteList> {
                val offset = it.page * Constants.PRODUCTS_PER_PAGE
                val vendors = favoriteService.findVendorsByUser(
                    session.userId!!,
                    Constants.PRODUCTS_PER_PAGE,
                    offset
                )

                call.respond(vendors)
            }

            post<AddFavorite> {
                val userId = session.userId!!
                if (it.id == userId)
                    throw BadRequestException()

                val vendor = userService.findById(it.id) ?: throw NotFoundException()

                favoriteService.addVendor(vendor.id!!, userId)
                call.respond(HttpStatusCode.OK)
            }

            delete<DeleteFavorite> {
                favoriteService.removeVendor(it.id, session.userId!!)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

data class FavoriteList(val page: Int = 0, val order: Int? = null)

data class DeleteFavorite(val id: Long)

data class AddFavorite(val id: Long)
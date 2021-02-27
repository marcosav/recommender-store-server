package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.CartService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.cart() {

    route("/cart") {

        val cartService by di().instance<CartService>()

        val userId = 1L

        get {
            val cart = cartService.findByUser(userId)
            call.respond(cart)
        }

        post<UpdateCartProduct> {
            // check product is hidden or deleted
            // validate
            val product = cartService.setProductAmount(userId, it.productId, it.amount)
            call.respond(product ?: HttpStatusCode.OK)
        }

        delete<DeleteCartProduct> {
            cartService.remove(it.id, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/all") {
            cartService.clear(userId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

data class DeleteCartProduct(val id: Long)

data class UpdateCartProduct(val userId: Long, val productId: Long, val amount: Long)
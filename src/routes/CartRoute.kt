package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.validators.CartUpdateValidator
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
        val productService by di().instance<ProductService>()

        val cartUpdateValidator by di().instance<CartUpdateValidator>()

        get {
            val cart = cartService.findByUser(session.userId!!)
            call.respond(cart)
        }

        post<UpdateCartProduct> {
            val product = productService.findById(it.productId)
            if (product?.hidden != false)
                throw NotFoundException()

            cartUpdateValidator.validate(it)

            val cartProduct = cartService.setProductAmount(session.userId!!, it.productId, it.amount)
            call.respond(cartProduct ?: HttpStatusCode.OK)
        }

        delete<DeleteCartProduct> {
            cartService.remove(it.productId, session.userId!!)
            call.respond(HttpStatusCode.OK)
        }

        delete("/all") {
            cartService.clear(session.userId!!)
            call.respond(HttpStatusCode.OK)
        }
    }
}

data class DeleteCartProduct(val productId: Long)

data class UpdateCartProduct(val userId: Long, val productId: Long, val amount: Long)
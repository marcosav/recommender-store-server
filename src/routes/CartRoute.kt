package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.cart.ICartService
import com.gmail.marcosav2010.services.cart.SessionCartService
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.validators.CartUpdateValidator
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

@KtorExperimentalLocationsAPI
fun Route.cart() {

    route("/cart") {

        val loggedCS by di().instance<CartService>()
        val productService by di().instance<ProductService>()

        val cartUpdateValidator by di().instance<CartUpdateValidator>()

        get {
            assertIdentified()

            val cart = loggedCS.findByUser(session.userId!!)
            call.respond(cart)
        }

        post<UpdateCartProduct> {
            val product = productService.findById(it.productId)
            if (product?.hidden != false)
                throw NotFoundException()

            cartUpdateValidator.validate(it)

            val cart = getCartService(loggedCS).setProductAmount(
                session.userId!!,
                it.productId,
                it.amount
            )

            call.respond(cart ?: HttpStatusCode.OK)
        }

        delete<DeleteCartProduct> {
            val cart = getCartService(loggedCS).remove(it.productId, session.userId!!)
            call.respond(cart ?: HttpStatusCode.OK)
        }

        delete("/all") {
            val cart = getCartService(loggedCS).clear(session.userId!!)
            call.respond(cart ?: HttpStatusCode.OK)
        }
    }
}

private fun PipelineContext<*, ApplicationCall>.getCartService(
    loggedCartService: CartService
): ICartService =
    if (session.userId != null) loggedCartService else SessionCartService(di(), session)

data class DeleteCartProduct(val productId: Long)

data class UpdateCartProduct(val userId: Long, val productId: Long, val amount: Long)
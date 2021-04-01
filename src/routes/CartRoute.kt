package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.services.FavoriteService
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.validators.CartUpdateValidator
import io.ktor.application.*
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
        val favoriteService by di().instance<FavoriteService>()

        val cartUpdateValidator by di().instance<CartUpdateValidator>()

        get {
            val cart = cartService.getCurrentCart(session)
            session.userId?.let { uid ->
                cart.onEach { it.product.fav = favoriteService.isFavoriteProduct(it.product.id, uid) }
            }
            call.respond(cart)
        }

        post<UpdateCartProduct> {
            val product = productService.findById(it.productId)
            if (product?.hidden != false)
                throw NotFoundException()

            it.product = product
            cartUpdateValidator.validate(it)

            val cart = cartService.updateProductAmount(
                session,
                it.productId,
                it.amount,
                it.add
            )

            call.respond(cart.handleCart())
        }

        delete<DeleteCartProduct> {
            val cart = cartService.remove(session, it.productId)
            call.respond(cart.handleCart())
        }

        delete("/all") {
            val cart = cartService.clear(session)
            call.respond(cart.handleCart())
        }
    }
}

private fun String.handleCart() = CartResponse(this)

data class CartResponse(val token: String)

data class DeleteCartProduct(val productId: Long)

data class UpdateCartProduct(val productId: Long, val amount: Long, val add: Boolean = false) {
    lateinit var product: Product
}
package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.services.CheckoutService
import com.gmail.marcosav2010.services.ProductService
import com.gmail.marcosav2010.services.assertIdentified
import com.gmail.marcosav2010.services.cart.CartService
import com.gmail.marcosav2010.services.session
import com.gmail.marcosav2010.validators.UserAddressValidator
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

@KtorExperimentalLocationsAPI
fun Route.checkout() {

    val cartService by closestDI().instance<CartService>()
    val addressValidator by closestDI().instance<UserAddressValidator>()
    val productService by closestDI().instance<ProductService>()
    val checkoutService by closestDI().instance<CheckoutService>()

    route("/checkout") {
        post<Address> {
            assertIdentified()

            val cartProductsIds = cartService.findByUser(session.userId!!)
            if (cartProductsIds.isEmpty())
                throw BadRequestException("empty")

            val products = mutableMapOf<Product, CartProduct>()
            if (cartProductsIds.any { cp ->
                    val p = productService.findById(cp.id) ?: return@any true
                    products[p] = cp
                    cp.amount > p.stock || p.hidden
                })
                throw BadRequestException("invalid")

            addressValidator.validate(it)

            val result = checkoutService.commit(session, it, products)

            call.respond(mapOf("orderId" to result.first, "token" to result.second))
        }
    }
}

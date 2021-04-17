package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.services.OrderService
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
    val orderService by closestDI().instance<OrderService>()

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

            products.forEach { (p, cp) ->
                productService.update(p.apply {
                    stock -= cp.amount
                    images = emptyList()
                })
            }

            val token = cartService.clear(session)

            val orderId = orderService.create(session.userId!!, it, products)

            call.respond(mapOf("orderId" to orderId, "token" to token))
        }
    }
}

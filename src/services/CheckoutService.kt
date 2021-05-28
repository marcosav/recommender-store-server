package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.services.cart.CartService
import org.kodein.di.DI
import org.kodein.di.instance

class CheckoutService(di: DI) {

    private val orderService by di.instance<OrderService>()
    private val feedbackService by di.instance<FeedbackService>()
    private val productService by di.instance<ProductService>()
    private val cartService by di.instance<CartService>()

    suspend fun commit(session: Session, address: Address, products: Map<Product, CartProduct>): Pair<Long, String> {
        products.forEach { (p, cp) ->
            productService.update(p.apply {
                stock -= cp.amount
                images = emptyList()
            })
        }

        val token = cartService.clear(session)

        val orderId = orderService.create(session.userId!!, address, products)

        feedbackService.collectBuy(session, products.map { p -> p.key.id!! })

        return Pair(orderId, token)
    }
}
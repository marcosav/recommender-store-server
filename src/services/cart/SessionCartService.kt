package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.model.SessionCart
import com.gmail.marcosav2010.services.AuthenticationService
import com.gmail.marcosav2010.services.ProductService
import org.kodein.di.DI
import org.kodein.di.instance

class SessionCartService(di: DI, private val session: Session) : ICartService {

    private val authService by di.instance<AuthenticationService>()
    private val productService by di.instance<ProductService>()

    private val cart: SessionCart = session.cart!!

    override fun updateProductAmount(userId: Long, productId: Long, amount: Long, add: Boolean): String {
        if (!add && amount == 0L) {
            remove(userId, productId)

        } else with(cart.find { it.id == productId }) {
            if (this != null)
                this.amount = if (add) this.amount + amount else amount
            else
                productService.findById(productId)?.let { cart.add(CartProduct(null, it, amount)) }
        }

        return updatedToken()
    }

    override fun addMultipleItems(userId: Long, items: List<CartProduct>): String {
        cart.addAll(items)
        return updatedToken()
    }

    override fun remove(userId: Long, productId: Long): String {
        cart.removeIf { it.id == productId }
        return updatedToken()
    }

    override fun clear(userId: Long): String {
        cart.clear()
        return updatedToken()
    }

    private fun updatedToken() = authService.token(session.sessionId, cart);
}
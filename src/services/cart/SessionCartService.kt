package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.model.SessionCart
import com.gmail.marcosav2010.services.ProductService
import org.kodein.di.DI
import org.kodein.di.instance

class SessionCartService(di: DI) : ICartService {

    private val productService by di.instance<ProductService>()

    override fun updateProductAmount(session: Session, productId: Long, amount: Long, add: Boolean): SessionCart {
        val cart = session.mCart
        if (!add && amount == 0L) {
            return remove(session, productId)

        } else with(cart.find { it.product.id == productId }) {
            if (this != null)
                this.amount = if (add) this.amount + amount else amount
            else
                productService.findByIdPreview(productId)?.let { cart.add(CartProduct(it, amount)) }
        }

        return cart
    }

    override fun remove(session: Session, productId: Long): SessionCart =
        session.mCart.apply { removeIf { it.product.id == productId } }

    override fun clear(session: Session): SessionCart = emptyList()

    private val Session.mCart
        get() = cart.toMutableList()
}
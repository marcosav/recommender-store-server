package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProduct

class SessionCartService : ICartService {

    fun findItems(): List<CartProduct> {
        return emptyList()
    }

    override fun setProductAmount(userId: Long, productId: Long, amount: Long): CartProduct? {
        return null
    }

    override fun addMultipleItems(userId: Long, items: List<CartProduct>) {

    }

    override fun remove(userId: Long, productId: Long) {

    }

    override fun clear(userId: Long) {

    }
}
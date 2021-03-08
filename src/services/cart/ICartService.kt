package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProduct

interface ICartService {

    fun updateProductAmount(userId: Long, productId: Long, amount: Long, add: Boolean): Any?

    fun addMultipleItems(userId: Long, items: List<CartProduct>): Any?

    fun remove(userId: Long, productId: Long): Any?

    fun clear(userId: Long): Any?
}
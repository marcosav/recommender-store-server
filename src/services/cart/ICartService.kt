package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.model.CartProduct

interface ICartService {

    fun setProductAmount(userId: Long, productId: Long, amount: Long): CartProduct?

    fun addMultipleItems(userId: Long, items: List<CartProduct>)

    fun remove(userId: Long, productId: Long)

    fun clear(userId: Long)
}
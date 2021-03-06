package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.db.dao.CartProductEntity
import com.gmail.marcosav2010.model.CartProduct
import org.jetbrains.exposed.sql.transactions.transaction

class CartService : ICartService {

    fun findByUser(userId: Long): List<CartProduct> = transaction {
        CartProductEntity.findByUser(userId).map { it.toCartProduct() }
    }

    override fun setProductAmount(userId: Long, productId: Long, amount: Long): Any? = transaction {
        if (amount == 0L)
            remove(userId, productId)
        else
            CartProductEntity.add(userId, productId, amount).toCartProduct()

        null
    }

    override fun addMultipleItems(userId: Long, items: List<CartProduct>): Unit? = transaction {
        items.forEach { it.product.id?.let { id -> CartProductEntity.add(userId, id, it.amount) } }
        null
    }

    override fun remove(userId: Long, productId: Long): Unit? = transaction {
        CartProductEntity.delete(userId, productId)
        null
    }

    override fun clear(userId: Long): Unit? = transaction {
        CartProductEntity.clear(userId)
        null
    }
}
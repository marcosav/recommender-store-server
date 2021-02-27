package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.CartProductEntity
import com.gmail.marcosav2010.model.CartProduct
import org.jetbrains.exposed.sql.transactions.transaction

class CartService : ServiceBase() {

    fun findByUser(userId: Long): List<CartProduct> = transaction {
        CartProductEntity.findByUser(userId).map { it.toCartProduct() }
    }

    fun setProductAmount(userId: Long, productId: Long, amount: Long): CartProduct? = transaction {
        if (amount == 0L) {
            remove(userId, productId)
            null
        } else
            CartProductEntity.add(userId, productId, amount).toCartProduct()
    }

    fun remove(userId: Long, productId: Long): Unit = transaction {
        CartProductEntity.delete(userId, productId)
    }

    fun clear(userId: Long): Unit = transaction {
        CartProductEntity.clear(userId)
    }
}
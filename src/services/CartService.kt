package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.CartProductEntity
import com.gmail.marcosav2010.model.CartProduct
import org.jetbrains.exposed.sql.transactions.transaction

class CartService : ServiceBase() {

    fun setProductAmount(productId: Long, userId: Long, amount: Long): CartProduct = transaction {
        CartProductEntity.setProductAmount(productId, userId, amount).toCartProduct()
    }

    fun findByUser(userId: Long): List<CartProduct> = transaction {
        CartProductEntity.findByUser(userId).map { it.toCartProduct() }
    }
}
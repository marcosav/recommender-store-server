package com.gmail.marcosav2010.services.cart

import com.gmail.marcosav2010.db.dao.CartProductEntity
import com.gmail.marcosav2010.model.Session
import com.gmail.marcosav2010.model.SessionCart
import org.jetbrains.exposed.sql.transactions.transaction

class LoggedCartService : ICartService {

    fun findByUser(userId: Long): SessionCart = transaction {
        CartProductEntity.findByUser(userId).map { it.toCartProduct() }
    }

    override fun updateProductAmount(session: Session, productId: Long, amount: Long, add: Boolean): SessionCart =
        transaction {
            val userId = session.userId!!
            val current = CartProductEntity.findByUserAndProduct(userId, productId).firstOrNull()
            if (current != null) {
                if (amount > 0 && add)
                    CartProductEntity.increaseAmount(current.id.value, amount)
                else if (amount == 0L && !add)
                    remove(session, productId)
            } else if (amount > 0L)
                CartProductEntity.add(userId, productId, amount).toCartProduct()

            findByUser(userId)
        }

    fun mergeCarts(userId: Long, oldCart: SessionCart): SessionCart = transaction {
        val userCart = findByUser(userId)
        if (userCart.isEmpty())
            oldCart.forEach { it.product.id?.let { id -> CartProductEntity.add(userId, id, it.amount) } }
        userCart
    }

    override fun remove(session: Session, productId: Long): SessionCart = transaction {
        val userId = session.userId!!
        CartProductEntity.delete(userId, productId)
        findByUser(userId)
    }

    override fun clear(session: Session): SessionCart = transaction {
        CartProductEntity.clear(session.userId!!)
        emptyList()
    }
}
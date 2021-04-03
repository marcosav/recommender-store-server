package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.CartProductPreview
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object CartProducts : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val amount = integer("amount")
    val lastUpdated = timestamp("last_updated")

    init {
        uniqueIndex(user, product)
    }
}

class CartProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<CartProductEntity>(CartProducts) {

        fun findByUser(userId: Long) = find { CartProducts.user eq userId }

        fun findByUserAndProduct(userId: Long, productId: Long) =
            find { (CartProducts.user eq userId) and (CartProducts.product eq productId) }

        fun add(userId: Long, productId: Long, amount: Int) =
            new {
                product = ProductEntity[productId]
                user = UserEntity[userId]
                this.amount = amount
                lastUpdated = Instant.now()
            }

        fun increaseAmount(id: Long, amount: Int) =
            CartProducts.update({ CartProducts.id eq id }) {
                with(SqlExpressionBuilder) {
                    it.update(CartProducts.amount, CartProducts.amount + amount)
                    it[lastUpdated] = Instant.now()
                }
            }

        fun update(id: Long, amount: Int) = CartProductEntity[id].apply {
            this.amount = amount
            this.lastUpdated = Instant.now()
        }

        fun clear(userId: Long) = CartProducts.deleteWhere { CartProducts.user eq userId }

        fun delete(userId: Long, productId: Long) =
            CartProducts.deleteWhere { (CartProducts.user eq userId) and (CartProducts.product eq productId) }
    }

    var user by UserEntity referencedOn CartProducts.user
    var product by ProductEntity referencedOn CartProducts.product
    var amount by CartProducts.amount
    var lastUpdated by CartProducts.lastUpdated

    fun toCartProduct() = CartProduct(product.id.value, amount)

    fun toPreview() = CartProductPreview(product.toPreviewProduct(), amount)
}
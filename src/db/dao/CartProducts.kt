package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.CartProduct
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object CartProducts : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val amount = long("amount")
    val lastUpdated = timestamp("last_updated").default(Instant.now())

    init {
        uniqueIndex(user, product)
    }
}

class CartProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<CartProductEntity>(CartProducts) {

        fun findByUser(userId: Long) = find { CartProducts.user eq userId }

        fun setProductAmount(userId: Long, productId: Long, amount: Long) =
            new {
                product = ProductEntity[productId]
                user = UserEntity[userId]
                this.amount = amount
                lastUpdated = Instant.now()
            }
    }

    var user by UserEntity referencedOn CartProducts.user
    var product by ProductEntity referencedOn CartProducts.product
    var amount by CartProducts.amount
    var lastUpdated by CartProducts.lastUpdated

    fun toCartProduct() = CartProduct(product.toProduct(), user.id.value, amount, lastUpdated)
}
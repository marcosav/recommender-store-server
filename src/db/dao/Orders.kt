package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.Order
import com.gmail.marcosav2010.model.OrderedProduct
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Orders : LongIdTable() {
    val date = timestamp("date")
    val user = reference("user", Users)
    val address = reference("address", Addresses)
}

class OrderEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<OrderEntity>(Orders) {

        fun add(userId: Long, addressId: Long) = new {
            user = UserEntity[userId]
            this.address = AddressEntity[addressId]
            date = Instant.now()
        }

        fun findByUser(userId: Long) = find { Orders.user eq userId }
    }

    var date by Orders.date
    var user by UserEntity referencedOn Orders.user
    var address by AddressEntity referencedOn Orders.address
    val products by OrderedProductEntity referrersOn OrderedProducts.order

    fun toOrder() = Order(id.value, user.id.value, address.toAddress(), products.map { it.toOrderedProduct() }, date)
}

object OrderedProducts : LongIdTable() {
    val amount = integer("amount")
    val unitPrice = double("unit_price")
    val product = reference("product", Products)
    val order = reference("order", Orders)
}

class OrderedProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<OrderedProductEntity>(OrderedProducts) {

        fun add(amount: Int, unitPrice: Double, productId: Long, orderId: Long) = new {
            this.amount = amount
            this.unitPrice = unitPrice
            product = ProductEntity[productId]
            order = OrderEntity[orderId]
        }
    }

    var amount by OrderedProducts.amount
    var unitPrice by OrderedProducts.unitPrice
    var product by ProductEntity referencedOn OrderedProducts.product
    var order by OrderEntity referencedOn OrderedProducts.order

    fun toOrderedProduct() = OrderedProduct(amount, unitPrice, product.toPreviewProduct())
}
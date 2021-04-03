package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.db.dao.AddressEntity
import com.gmail.marcosav2010.db.dao.OrderEntity
import com.gmail.marcosav2010.db.dao.OrderedProductEntity
import com.gmail.marcosav2010.db.dao.Orders
import com.gmail.marcosav2010.model.Address
import com.gmail.marcosav2010.model.CartProduct
import com.gmail.marcosav2010.model.Order
import com.gmail.marcosav2010.model.Product
import db.Paged
import db.paged
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

class OrderService {

    fun findByUser(userId: Long, size: Int, offset: Int): Paged<Order> = transaction {
        OrderEntity.findByUser(userId).paged(
            { it.toOrder() },
            size,
            offset,
            Pair(Orders.date, SortOrder.DESC)
        )
    }

    fun create(userId: Long, address: Address, cartProducts: Map<Product, CartProduct>): Long = transaction {
        val addressId = AddressEntity.add(address).id.value
        val orderId = OrderEntity.add(userId, addressId).id.value
        cartProducts.forEach { (p, cp) -> OrderedProductEntity.add(cp.amount, p.price, p.id!!, orderId) }
        orderId
    }

    fun findById(id: Long) = transaction { OrderEntity.findById(id) }
}
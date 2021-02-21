package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Carts : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE).uniqueIndex()
}

class CartRelation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CartRelation>(Carts)

    var user by UserEntity referencedOn Carts.user
    val cartProducts by CartProductRelation referrersOn CartProducts.cart
}

object CartProducts : LongIdTable() {
    val cart = reference("cart", Carts, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val amount = integer("amount")
    val lastUpdated = timestamp("last_updated").default(Instant.now())
}

class CartProductRelation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CartProductRelation>(CartProducts)

    var cart by CartRelation referencedOn CartProducts.cart
    var product by ProductEntity referencedOn CartProducts.product
    var amount by CartProducts.amount
    var lastUpdated by CartProducts.lastUpdated
}
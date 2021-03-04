package com.gmail.marcosav2010.db.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object FavoriteProducts : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val product = reference("product", Products, onDelete = ReferenceOption.CASCADE)
    val date = timestamp("date").default(Instant.now())

    init {
        uniqueIndex(user, product)
    }
}

class FavoriteProductEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<FavoriteProductEntity>(FavoriteProducts) {

        // TODO: check this
        fun findByUser(userId: Long) =
            ProductEntity.find {
                Products.id inSubQuery (FavoriteProducts.slice(FavoriteProducts.id)
                    .select { FavoriteProducts.user eq userId })
            }.orderBy(Pair(FavoriteProducts.date, SortOrder.DESC))

        fun findByUserAndProduct(userId: Long, productId: Long) =
            find { (FavoriteProducts.user eq userId) and (FavoriteProducts.product eq productId) }

        fun add(userId: Long, productId: Long) =
            new {
                product = ProductEntity[productId]
                user = UserEntity[userId]
            }

        fun delete(userId: Long, productId: Long) =
            FavoriteProducts.deleteWhere { (FavoriteProducts.user eq userId) and (FavoriteProducts.product eq productId) }
    }

    var user by UserEntity referencedOn FavoriteProducts.user
    var product by ProductEntity referencedOn FavoriteProducts.product
    var date by FavoriteProducts.date
}